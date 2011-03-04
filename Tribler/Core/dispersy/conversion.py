from socket import inet_ntoa, inet_aton
from struct import pack, unpack_from
from hashlib import sha1

from authentication import NoAuthentication, MemberAuthentication, MultiMemberAuthentication
from bloomfilter import BloomFilter
from destination import MemberDestination, CommunityDestination, AddressDestination, SubjectiveDestination, SimilarityDestination
from dispersydatabase import DispersyDatabase
from distribution import FullSyncDistribution, LastSyncDistribution, DirectDistribution, RelayDistribution
from encoding import encode, decode
from member import PrivateMember, MasterMember
from message import DelayPacket, DelayPacketByMissingMember, DropPacket, Message
from resolution import LinearResolution

if __debug__:
    from dprint import dprint

class Conversion(object):
    """
    A Conversion object is used to convert incoming packets to a different, possibly more recent,
    community version.  If also allows outgoing messages to be converted to a different, possibly
    older, community version.
    """
    def __init__(self, community, version):
        """
        COMMUNITY instance that this conversion belongs to.
        VERSION is the conversion identifyer (on the wire version).
        """
        if __debug__: from community import Community
        assert isinstance(community, Community), type(community)
        assert isinstance(version, str), type(version)
        assert len(version) == 2, version

        # the dispersy database
        self._dispersy_database = DispersyDatabase.get_instance()

        # the community that this conversion belongs to.
        self._community = community

        # the messages that this instance can handle, and that this instance produces, is identified
        # by _prefix.
        self._prefix = community.cid + version

    @property
    def community(self):
        return self._community

    @property
    def version(self):
        return self._prefix[20:22]

    @property
    def prefix(self):
        return self._prefix

    def decode_message(self, data):
        """
        DATA is a string, where the first 20 bytes indicate the CID,
        the next 2 bytes the on-the-wite VERSION, and the rest forms
        the message payload.

        Returns a Message instance.
        """
        assert isinstance(data, str)
        assert len(data) >= 22
        assert data[:22] == self._prefix
        raise NotImplementedError("The subclass must implement decode_message")

    def encode_message(self, message):
        """
        Encode a Message instance into a binary string that starts
        with CID and the on-the-wire VERSION.
        """
        assert isinstance(message, Message)
        raise NotImplementedError("The subclass must implement encode_message")

class BinaryConversion(Conversion):
    """
    On-The-Wire binary version

    This conversion is intended to be as space efficient as possible.
    All data is encoded in a binary form.
    """
    def __init__(self, community, version):
        Conversion.__init__(self, community, version)
        self._encode_distribution_map = {FullSyncDistribution.Implementation:self._encode_full_sync_distribution,
                                         LastSyncDistribution.Implementation:self._encode_last_sync_distribution,
                                         DirectDistribution.Implementation:self._encode_direct_distribution}
        self._decode_distribution_map = {FullSyncDistribution:self._decode_full_sync_distribution,
                                         LastSyncDistribution:self._decode_last_sync_distribution,
                                         DirectDistribution:self._decode_direct_distribution}
        self._encode_message_map = dict() # message.name : (byte, encode_payload_func)
        self._decode_message_map = dict() # byte : (message, decode_payload_func)

        def define(value, name, encode, decode):
            try:
                meta = community.get_meta_message(name)
            except KeyError:
                if __debug__: dprint("unable to define non-available message ", name, level="warning")
            else:
                self.define_meta_message(chr(value), meta, encode, decode)

        define(254, u"dispersy-missing-sequence", self._encode_missing_sequence, self._decode_missing_sequence)
        define(253, u"dispersy-sync", self._encode_sync, self._decode_sync)
        define(252, u"dispersy-signature-request", self._encode_signature_request, self._decode_signature_request)
        define(251, u"dispersy-signature-response", self._encode_signature_response, self._decode_signature_response)
        define(250, u"dispersy-candidate-request", self._encode_candidate_request, self._decode_candidate_request)
        define(249, u"dispersy-candidate-response", self._encode_candidate_response, self._decode_candidate_response)
        define(248, u"dispersy-identity", self._encode_identity, self._decode_identity)
        define(247, u"dispersy-identity-request", self._encode_identity_request, self._decode_identity_request)
#         define(246, u"dispersy-similarity", self._encode_similarity, self._decode_similarity)
#         define(245, u"dispersy-similarity-request", self._encode_similarity_request, self._decode_similarity_request)
        define(244, u"dispersy-destroy-community", self._encode_destroy_community, self._decode_destroy_community)
        define(243, u"dispersy-authorize", self._encode_authorize, self._decode_authorize)
        define(242, u"dispersy-revoke", self._encode_revoke, self._decode_revoke)
        define(241, u"dispersy-subjective-set", self._encode_subjective_set, self._decode_subjective_set)
        define(240, u"dispersy-subjective-set-request", self._encode_subjective_set_request, self._decode_subjective_set_request)

#         self.define_meta_message(chr(254), community.get_meta_message(u"dispersy-missing-sequence"), self._encode_missing_sequence, self._decode_missing_sequence)
#         self.define_meta_message(chr(253), community.get_meta_message(u"dispersy-sync"), self._encode_sync, self._decode_sync)
#         self.define_meta_message(chr(252), community.get_meta_message(u"dispersy-signature-request"), self._encode_signature_request, self._decode_signature_request)
#         self.define_meta_message(chr(251), community.get_meta_message(u"dispersy-signature-response"), self._encode_signature_response, self._decode_signature_response)
#         self.define_meta_message(chr(250), community.get_meta_message(u"dispersy-candidate-request"), self._encode_candidate_request, self._decode_candidate_request)
#         self.define_meta_message(chr(249), community.get_meta_message(u"dispersy-candidate-response"), self._encode_candidate_response, self._decode_candidate_response)
#         self.define_meta_message(chr(248), community.get_meta_message(u"dispersy-identity"), self._encode_identity, self._decode_identity)
#         self.define_meta_message(chr(247), community.get_meta_message(u"dispersy-identity-request"), self._encode_identity_request, self._decode_identity_request)
# #         self.define_meta_message(chr(246), community.get_meta_message(u"dispersy-similarity"), self._encode_similarity, self._decode_similarity)
# #         self.define_meta_message(chr(245), community.get_meta_message(u"dispersy-similarity-request"), self._encode_similarity_request, self._decode_similarity_request)
#         self.define_meta_message(chr(244), community.get_meta_message(u"dispersy-destroy-community"), self._encode_destroy_community, self._decode_destroy_community)
#         self.define_meta_message(chr(243), community.get_meta_message(u"dispersy-authorize"), self._encode_authorize, self._decode_authorize)
#         self.define_meta_message(chr(242), community.get_meta_message(u"dispersy-revoke"), self._encode_revoke, self._decode_revoke)
#         self.define_meta_message(chr(241), community.get_meta_message(u"dispersy-subjective-set"), self._encode_subjective_set, self._decode_subjective_set)
#         self.define_meta_message(chr(240), community.get_meta_message(u"dispersy-subjective-set-request"), self._encode_subjective_set_request, self._decode_subjective_set_request)

    def define_meta_message(self, byte, message, encode_payload_func, decode_payload_func):
        assert isinstance(byte, str)
        assert len(byte) == 1
        assert isinstance(message, Message)
        assert 0 < ord(byte) < 255
        assert not message.name in self._encode_message_map
        assert not byte in self._decode_message_map, "This byte has already been defined (%d)" % ord(byte)
        assert callable(encode_payload_func)
        assert callable(decode_payload_func)
        self._encode_message_map[message.name] = (byte, encode_payload_func)
        self._decode_message_map[byte] = (message, decode_payload_func)

    #
    # Dispersy payload
    #

    def _encode_missing_sequence(self, message):
        payload = message.payload
        assert payload.message.name in self._encode_message_map, payload.message.name
        message_id, _ = self._encode_message_map[payload.message.name]
        return payload.member.mid, message_id, pack("!LL", payload.missing_low, payload.missing_high)

    def _decode_missing_sequence(self, meta_message, offset, data):
        if len(data) < offset + 29:
            raise DropPacket("Insufficient packet size")

        member_id = data[offset:offset+20]
        offset += 20
        members = self._community.get_members_from_id(member_id)
        if not members:
            raise DelayPacketByMissingMember(self._community, member_id)
        elif len(members) > 1:
            # this is unrecoverable.  a member id without a signature is simply not globally unique.
            # This can occur when two or more nodes have the same sha1 hash.  Very unlikely.
            raise DropPacket("Unrecoverable: ambiguous member")
        member = members[0]

        missing_meta_message, _ = self._decode_message_map.get(data[offset], (None, None))
        if missing_meta_message is None:
            raise DropPacket("Invalid message")
        offset += 1

        missing_low, missing_high = unpack_from("!LL", data, offset)
        offset += 8

        return offset, meta_message.payload.implement(member, missing_meta_message, missing_low, missing_high)

    def _encode_sync(self, message):
        return pack("!QQ", message.payload.time_low, message.payload.time_high), str(message.payload.bloom_filter)

    def _decode_sync(self, meta_message, offset, data):
        if len(data) < offset + 16:
            raise DropPacket("Insufficient packet size")

        time_low, time_high = unpack_from("!QQ", data, offset)
        offset += 16
        if not time_low > 0:
            raise DropPacket("Invalid time_low value")
        if not (time_high == 0 or time_low <= time_high):
            raise DropPacket("Invalid time_high value")

        try:
            bloom_filter = BloomFilter(data, offset)
        except ValueError:
            raise DropPacket("Invalid bloom filter")
        offset += len(bloom_filter)

        return offset, meta_message.payload.implement(time_low, time_high, bloom_filter)

    def _encode_signature_request(self, message):
        return self.encode_message(message.payload.message),

    def _decode_signature_request(self, meta_message, offset, data):
        return len(data), meta_message.payload.implement(self._decode_message(data[offset:], False))

    def _encode_signature_response(self, message):
        return message.payload.identifier, message.payload.signature

    def _decode_signature_response(self, meta_message, offset, data):
        return len(data), meta_message.payload.implement(data[offset:offset+20], data[offset+20:])

    def _encode_candidate_request(self, message):
        bytes = [inet_aton(message.payload.source_address[0]), pack("!H", message.payload.source_address[1]),
                 inet_aton(message.payload.destination_address[0]), pack("!H", message.payload.destination_address[1]),
                 message.payload.source_default_conversion]
        for address, age in message.payload.routes:
            bytes.extend((inet_aton(address[0]), pack("!HB", address[1], int(min(255, age)))))
        return bytes

    def _decode_candidate_request(self, meta_message, offset, data):
        if len(data) < offset + 14:
            raise DropPacket("Insufficient packet size")

        source_address = (inet_ntoa(data[offset:offset+4]), unpack_from("!H", data, offset+4)[0])
        offset += 6
        destination_address = (inet_ntoa(data[offset:offset+4]), unpack_from("!H", data, offset+4)[0])
        offset += 6
        source_default_conversion = data[offset:offset+2]
        offset += 2

        routes = []
        while len(data) >= offset + 7:
            host, (port, age) = (inet_ntoa(data[offset:offset+4]), unpack_from("!HB", data, offset+4))
            offset += 7
            routes.append(((host, port), float(age)))

        return offset, meta_message.payload.implement(source_address, destination_address, source_default_conversion, routes)

    def _encode_candidate_response(self, message):
        bytes = [message.payload.request_identifier,
                 inet_aton(message.payload.source_address[0]), pack("!H", message.payload.source_address[1]),
                 inet_aton(message.payload.destination_address[0]), pack("!H", message.payload.destination_address[1]),
                 message.payload.source_default_conversion]
        for address, age in message.payload.routes:
            bytes.extend((inet_aton(address[0]), pack("!HB", address[1], int(min(255, age)))))
        return bytes

    def _decode_candidate_response(self, meta_message, offset, data):
        if len(data) < offset + 32:
            raise DropPacket("Insufficient packet size")

        request_identifier = data[offset:offset+20]
        offset += 20
        source_address = (inet_ntoa(data[offset:offset+4]), unpack_from("!H", data, offset+4)[0])
        offset += 6
        destination_address = (inet_ntoa(data[offset:offset+4]), unpack_from("!H", data, offset+4)[0])
        offset += 6
        source_default_conversion = data[offset:offset+2]
        offset += 2

        routes = []
        while len(data) >= offset + 7:
            host, (port, age) = (inet_ntoa(data[offset:offset+4]), unpack_from("!HB", data, offset+4))
            offset += 7
            routes.append(((host, port), float(age)))

        return offset, meta_message.payload.implement(request_identifier, source_address, destination_address, source_default_conversion, routes)

    def _encode_identity(self, message):
        return inet_aton(message.payload.address[0]), pack("!H", message.payload.address[1])

    def _decode_identity(self, meta_message, offset, data):
        if len(data) < offset + 6:
            raise DropPacket("Insufficient packet size")

        address = (inet_ntoa(data[offset:offset+4]), unpack_from("!H", data, offset+4)[0])

        return offset + 6, meta_message.payload.implement(address)

    def _encode_identity_request(self, message):
        return message.payload.mid,

    def _decode_identity_request(self, meta_message, offset, data):
        if len(data) < offset + 20:
            raise DropPacket("Insufficient packet size")

        return offset + 20, meta_message.payload.implement(data[offset:offset+20])

    def _encode_similarity(self, message):
        return pack("!B", message.payload.cluster), str(message.payload.similarity)

    def _decode_similarity(self, meta_message, offset, data):
        if len(data) < offset + 1:
            raise DropPacket("Insufficient packet size")

        cluster, = unpack_from("!B", data, offset)
        offset += 1

        try:
            similarity = BloomFilter(data, offset)
        except ValueError:
            raise DropPacket("Invalid similarity")
        offset += len(similarity)

        return offset, meta_message.payload.implement(cluster, similarity)

    def _encode_similarity_request(self, message):
        return (pack("!B", message.payload.cluster),) + tuple([member.mid for member in message.payload.members])

    def _decode_similarity_request(self, meta_message, offset, data):
        if len(data) < offset + 21:
            raise DropPacket("Insufficient packet size")

        cluster, = unpack_from("!B", data, offset)
        offset += 1

        members = []
        while len(data) < offset + 20:
            members.extend(self._community.get_members_from_id(data[offset:offset+20]))
            offset += 20

        return offset, meta_message.payload.implement(cluster, members)

    def _encode_destroy_community(self, message):
        if message.payload.is_soft_kill:
            return "s",
        else:
            return "h",

    def _decode_destroy_community(self, meta_message, offset, data):
        if len(data) < offset + 1:
            raise DropPacket("Insufficient packet size")

        if data[offset] == "s":
            degree = u"soft-kill"
        else:
            degree = u"hard-kill"
        offset += 1

        return offset, meta_message.payload.implement(degree)

    def _encode_authorize(self, message):
        """
        Encode the permissiong_triplets (Member, Message, permission) into an on-the-wire string.

        On-the-wire format:
        [ repeat for each Member
           2 byte member public key length
           n byte member public key
           1 byte length
           [ once for each number in previous byte
              1 byte message id
              1 byte permission bits
           ]
        ]
        """
        permission_map = {u"permit":1, u"authorize":2, u"revoke":4}
        members = {}
        for member, message, permission in message.payload.permission_triplets:
            public_key = member.public_key
            assert isinstance(public_key, str)
            assert message.name in self._encode_message_map
            message_id = self._encode_message_map[message.name][0]
            assert isinstance(message_id, str)
            assert len(message_id) == 1
            assert permission in permission_map
            permission_bit = permission_map[permission]

            if not public_key in members:
                members[public_key] = {}

            if not message_id in members[public_key]:
                members[public_key][message_id] = 0

            members[public_key][message_id] |= permission_bit

        bytes = []
        for public_key, messages in members.iteritems():
            bytes.extend((pack("!H", len(public_key)), public_key, pack("!B", len(messages))))
            for message_id, permission_bits in messages.iteritems():
                bytes.extend((message_id, pack("!B", permission_bits)))

        return tuple(bytes)

    def _decode_authorize(self, meta_message, offset, data):
        permission_map = {u"permit":1, u"authorize":2, u"revoke":4}
        permission_triplets = []

        while offset < len(data):
            if len(data) < offset + 2:
                raise DropPacket("Insufficient packet size")

            key_length, = unpack_from("!H", data, offset)
            offset += 2

            if len(data) < offset + key_length + 1:
                raise DropPacket("Insufficient packet size")

            member = self._community.get_member(data[offset:offset+key_length])
            offset += key_length

            messages_length, = unpack_from("!B", data, offset)
            offset += 1

            if len(data) < offset + messages_length * 2:
                raise DropPacket("Insufficient packet size")

            for _ in xrange(messages_length):
                message_id = data[offset]
                offset += 1
                if not message_id in self._decode_message_map:
                    raise DropPacket("Unknown message id")
                message = self._decode_message_map[message_id][0]

                if not isinstance(message.resolution, LinearResolution):
                    # it makes no sence to authorize a message that does not use the
                    # LinearResolution policy.  currently we have two policies, PublicResolution
                    # (where all messages are allowed regardless of authorization) and
                    # LinearResolution.
                    raise DropPacket("Invalid resolution policy")

                if not isinstance(message.authentication, MemberAuthentication):
                    # it makes no sence to authorize a message that does not use the
                    # MemberAuthentication policy because without this policy it is impossible to
                    # verify WHO created the message.
                    raise DropPacket("Invalid authentication policy")

                permission_bits, = unpack_from("!B", data, offset)
                offset += 1

                for permission, permission_bit in permission_map.iteritems():
                    if permission_bit & permission_bits:
                        permission_triplets.append((member, message, permission))

        return offset, meta_message.payload.implement(permission_triplets)

    def _encode_revoke(self, message):
        """
        Encode the permissiong_triplets (Member, Message, permission) into an on-the-wire string.

        On-the-wire format:
        [ repeat for each Member
           2 byte member public key length
           n byte member public key
           1 byte length
           [ once for each number in previous byte
              1 byte message id
              1 byte permission bits
           ]
        ]
        """
        permission_map = {u"permit":1, u"authorize":2, u"revoke":4}
        members = {}
        for member, message, permission in message.payload.permission_triplets:
            public_key = member.public_key
            assert isinstance(public_key, str)
            assert message.name in self._encode_message_map
            message_id = self._encode_message_map[message.name][0]
            assert isinstance(message_id, str)
            assert len(message_id) == 1
            assert permission in permission_map
            permission_bit = permission_map[permission]

            if not public_key in members:
                members[public_key] = {}

            if not message_id in members[public_key]:
                members[public_key][message_id] = 0

            members[public_key][message_id] |= permission_bit

        bytes = []
        for public_key, messages in members.iteritems():
            bytes.extend((pack("!H", len(public_key)), public_key, pack("!B", len(messages))))
            for message_id, permission_bits in messages.iteritems():
                bytes.extend((message_id, pack("!B", permission_bits)))

        return tuple(bytes)

    def _decode_revoke(self, meta_message, offset, data):
        permission_map = {u"permit":1, u"authorize":2, u"revoke":4}
        permission_triplets = []

        while offset < len(data):
            if len(data) < offset + 2:
                raise DropPacket("Insufficient packet size")

            key_length, = unpack_from("!H", data, offset)
            offset += 2

            if len(data) < offset + key_length + 1:
                raise DropPacket("Insufficient packet size")

            member = self._community.get_member(data[offset:offset+key_length])
            offset += key_length

            messages_length, = unpack_from("!B", data, offset)
            offset += 1

            if len(data) < offset + messages_length * 2:
                raise DropPacket("Insufficient packet size")

            for _ in xrange(messages_length):
                message_id = data[offset]
                offset += 1
                if not message_id in self._decode_message_map:
                    raise DropPacket("Unknown message id")
                message = self._decode_message_map[message_id][0]

                if not isinstance(message.resolution, LinearResolution):
                    # it makes no sence to authorize a message that does not use the
                    # LinearResolution policy.  currently we have two policies, PublicResolution
                    # (where all messages are allowed regardless of authorization) and
                    # LinearResolution.
                    raise DropPacket("Invalid resolution policy")

                if not isinstance(message.authentication, MemberAuthentication):
                    # it makes no sence to authorize a message that does not use the
                    # MemberAuthentication policy because without this policy it is impossible to
                    # verify WHO created the message.
                    raise DropPacket("Invalid authentication policy")

                permission_bits, = unpack_from("!B", data, offset)
                offset += 1

                for permission, permission_bit in permission_map.iteritems():
                    if permission_bit & permission_bits:
                        permission_triplets.append((member, message, permission))

        return offset, meta_message.payload.implement(permission_triplets)

    def _encode_subjective_set(self, message):
        return pack("!B", message.payload.cluster), str(message.payload.subjective_set)

    def _decode_subjective_set(self, meta_message, offset, data):
        if len(data) < offset + 1:
            raise DropPacket("Insufficient packet size")

        cluster, = unpack_from("!B", data, offset)
        offset += 1

        try:
            subjective_set = BloomFilter(data, offset)
        except ValueError:
            raise DropPacket("Invalid subjective set")
        offset += len(subjective_set)

        return offset, meta_message.payload.implement(cluster, subjective_set)

    def _encode_subjective_set_request(self, message):
        return (pack("!B", message.payload.cluster),) + tuple([member.mid for member in message.payload.members])

    def _decode_subjective_set_request(self, meta_message, offset, data):
        if len(data) < offset + 21:
            raise DropPacket("Insufficient packet size")

        cluster, = unpack_from("!B", data, offset)
        offset += 1

        members = []
        while len(data) < offset + 20:
            members.extend(self._community.get_members_from_id(data[offset:offset+20]))
            offset += 20

        return offset, meta_message.payload.implement(cluster, members)
    #
    # Encoding
    #

    @staticmethod
    def _encode_full_sync_distribution(container, message):
        if message.distribution.enable_sequence_number:
            container.append(pack("!QL", message.distribution.global_time, message.distribution.sequence_number))
        else:
            container.append(pack("!Q", message.distribution.global_time))

    @staticmethod
    def _encode_last_sync_distribution(container, message):
        if message.distribution.enable_sequence_number:
            container.append(pack("!QL", message.distribution.global_time, message.distribution.sequence_number))
        else:
            container.append(pack("!Q", message.distribution.global_time))

    @staticmethod
    def _encode_direct_distribution(container, message):
        container.append(pack("!Q", message.distribution.global_time))

    def encode_message(self, message):
        assert isinstance(message, Message.Implementation), message
        assert message.name in self._encode_message_map, message.name
        message_id, encode_payload_func = self._encode_message_map[message.name]

        # Community prefix, message-id
        container = [self._prefix, message_id]

        # Authentication
        if isinstance(message.authentication, NoAuthentication.Implementation):
            pass
        elif isinstance(message.authentication, MemberAuthentication.Implementation):
            if message.authentication.encoding == "sha1":
                container.append(message.authentication.member.mid)
            elif message.authentication.encoding == "bin":
                container.extend((pack("!H", len(message.authentication.member.public_key)), message.authentication.member.public_key))
            else:
                raise NotImplementedError(message.authentication.encoding)
        elif isinstance(message.authentication, MultiMemberAuthentication.Implementation):
            container.extend([member.mid for member in message.authentication.members])
        else:
            raise NotImplementedError(type(message.authentication))

        # Destination does not hold any space in the message

        # Distribution
        assert type(message.distribution) in self._encode_distribution_map
        self._encode_distribution_map[type(message.distribution)](container, message)

        if __debug__:
            dprint(message.name, "          head ", sum(map(len, container)) + 1, " bytes")

        # Payload
        itererator = encode_payload_func(message)
        assert isinstance(itererator, (tuple, list)), (type(itererator), encode_payload_func)
        assert not filter(lambda x: not isinstance(x, str), itererator)
        container.extend(itererator)

        if __debug__:
            dprint(message.name, "     head+body ", sum(map(len, container)), " bytes")

        # Sign
        if isinstance(message.authentication, NoAuthentication.Implementation):
            packet = "".join(container)

        elif isinstance(message.authentication, MemberAuthentication.Implementation):
            assert isinstance(message.authentication.member, PrivateMember)
            data = "".join(container)
            signature = message.authentication.member.sign(data)
            message.authentication.set_signature(signature)
            packet = data + signature

        elif isinstance(message.authentication, MultiMemberAuthentication.Implementation):
            data = "".join(container)
            signatures = []
            for signature, member in message.authentication.signed_members:
                if signature:
                    signatures.append(signature)
                elif isinstance(member, PrivateMember):
                    signature = member.sign(data)
                    message.authentication.set_signature(member, signature)
                    signatures.append(signature)
                else:
                    signatures.append("\x00" * member.signature_length)
            packet = data + "".join(signatures)

        else:
            raise NotImplementedError(type(message.authentication))

        if __debug__:
            dprint(message.name, " head+body+sig ", len(packet), " bytes")

        # dprint(message.packet.encode("HEX"))
        return packet

    #
    # Decoding
    #

    @staticmethod
    def _decode_full_sync_distribution(offset, data, meta_message):
        if meta_message.distribution.enable_sequence_number:
            global_time, sequence_number = unpack_from("!QL", data, offset)
            return offset + 12, meta_message.distribution.implement(global_time, sequence_number)
        else:
            global_time, = unpack_from("!Q", data, offset)
            return offset + 8, meta_message.distribution.implement(global_time)

    @staticmethod
    def _decode_last_sync_distribution(offset, data, meta_message):
        if meta_message.distribution.enable_sequence_number:
            global_time, sequence_number = unpack_from("!QL", data, offset)
            return offset + 12, meta_message.distribution.implement(global_time, sequence_number)
        else:
            global_time, = unpack_from("!Q", data, offset)
            return offset + 8, meta_message.distribution.implement(global_time)

    @staticmethod
    def _decode_direct_distribution(offset, data, meta_message):
        global_time, = unpack_from("!Q", data, offset)
        return offset + 8, meta_message.distribution.implement(global_time)

    def _decode_authentication(self, authentication, offset, data):
        if isinstance(authentication, NoAuthentication):
            return offset, authentication.implement(), len(data)

        elif isinstance(authentication, MemberAuthentication):
            if authentication.encoding == "sha1":
                member_id = data[offset:offset+20]
                members = self._community.get_members_from_id(member_id)
                if not members:
                    raise DelayPacketByMissingMember(self._community, member_id)
                offset += 20

                for member in members:
                    first_signature_offset = len(data) - member.signature_length
                    if member.verify(data, data[first_signature_offset:], length=first_signature_offset):
                        return offset, authentication.implement(member, is_signed=True), first_signature_offset

                raise DelayPacketByMissingMember(self._community, member_id)

            elif authentication.encoding == "bin":
                key_length, = unpack_from("!H", data, offset)
                offset += 2
                member = self._community.get_member(data[offset:offset+key_length])
                offset += key_length
                first_signature_offset = len(data) - member.signature_length
                if member.verify(data, data[first_signature_offset:], length=first_signature_offset):
                    return offset, authentication.implement(member, is_signed=True), first_signature_offset
                else:
                    raise DropPacket("Invalid signature")

            else:
                raise NotImplementedError(authentication.encoding)

        elif isinstance(authentication, MultiMemberAuthentication):
            def iter_options(members_ids):
                """
                members_ids = [[m1_a, m1_b], [m2_a], [m3_a, m3_b]]
                --> m1_a, m2_a, m3_a
                --> m1_a, m2_a, m3_b
                --> m1_b, m2_a, m3_a
                --> m1_b, m2_a, m3_b
                """
                if members_ids:
                    for member_id in members_ids[0]:
                        for others in iter_options(members_ids[1:]):
                            yield [member_id] + others
                else:
                    yield []

            members_ids = []
            for _ in range(authentication.count):
                member_id = data[offset:offset+20]
                members = self._community.get_members_from_id(member_id)
                if not members:
                    raise DelayPacketByMissingMember(self._community, member_id)
                offset += 20
                members_ids.append(members)

            for members in iter_options(members_ids):
                # try this member combination
                first_signature_offset = len(data) - sum([member.signature_length for member in members])
                signature_offset = first_signature_offset
                signatures = [""] * authentication.count
                valid_or_null = True
                for index, member in zip(range(authentication.count), members):
                    signature = data[signature_offset:signature_offset+member.signature_length]
                    # dprint("INDEX: ", index)
                    # dprint(signature.encode('HEX'))
                    if not signature == "\x00" * member.signature_length:
                        if member.verify(data, data[signature_offset:signature_offset+member.signature_length], length=first_signature_offset):
                            signatures[index] = signature
                        else:
                            valid_or_null = False
                            break
                    signature_offset += member.signature_length

                # found a valid combination
                if valid_or_null:
                    return offset, authentication.implement(members, signatures=signatures), first_signature_offset
            raise DelayPacketByMissingMember(self._community, member_id)

        raise NotImplementedError()

    def _decode_subjective_destination(self, meta_message, authentication_impl):
        # we want to know if the sender occurs in our subjective bloom filter
        try:
            subjective_set = meta_message.community.get_subjective_set(meta_message.community.my_member, meta_message.destination.cluster)
        except KeyError:
            # we do not yet have a subjective set of our own.  assume nothing is in the set
            return meta_message.destination.implement(False)
        else:
            return meta_message.destination.implement(authentication_impl.member.public_key in subjective_set)

    def _decode_similarity_destination(self, meta_message, authentication_impl):
        if __debug__:
            from authentication import Authentication
        assert isinstance(meta_message, Message)
        assert isinstance(authentication_impl, Authentication.Implementation)

        try:
            my_similarity, = self._dispersy_database.execute(u"SELECT similarity FROM similarity WHERE community = ? AND user = ? AND cluster = ?",
                                                             (self._community.database_id,
                                                              self._community._my_member.database_id,
                                                              meta_message.destination.cluster)).next()
        except StopIteration:
            raise DropPacket("We don't know our own similarity... should not happen")
        my_similarity = BloomFilter(str(my_similarity), 0)

        try:
            sender_similarity, = self._dispersy_database.execute(u"SELECT similarity FROM similarity WHERE community = ? AND user = ? AND cluster = ?",
                                                                 (self._community.database_id,
                                                                  authentication_impl.member.database_id,
                                                                  meta_message.destination.cluster)).next()
        except StopIteration:
            raise DelayPacketBySimilarity(self._community, authentication_impl.member, meta_message.destination)
        sender_similarity = BloomFilter(str(sender_similarity), 0)

        return meta_message.destination.implement(my_similarity.bic_occurrence(sender_similarity))

    def _decode_message(self, data, verify_all_signatures):
        """
        Decode a binary string into a Message structure, with some
        Dispersy specific parameters.

        When VERIFY_ALL_SIGNATURES is True, all signatures must be
        valid.  When VERIFY_ALL_SIGNATURES is False, signatures may be
        \x00 bytes.  Message.authentication.signed_members returns
        information on which members had a signature present.
        Signatures that are set and fail will NOT be accepted.
        """
        assert isinstance(data, str)
        assert isinstance(verify_all_signatures, bool)
        assert len(data) >= 22
        assert data[:22] == self._prefix, (data[:22].encode("HEX"), self._prefix.encode("HEX"))

        if len(data) < 100:
            DropPacket("Packet is to small to decode")

        offset = 22

        # meta_message
        meta_message, decode_payload_func = self._decode_message_map.get(data[offset], (None, None))
        if meta_message is None:
            raise DropPacket("Unknown message code %d" % ord(data[offset]))
        offset += 1

        # authentication
        offset, authentication_impl, first_signature_offset = self._decode_authentication(meta_message.authentication, offset, data)
        if verify_all_signatures and not authentication_impl.is_signed:
            raise DropPacket("Invalid signature")

        # destination
        assert isinstance(meta_message.destination, (MemberDestination, CommunityDestination, AddressDestination, SubjectiveDestination, SimilarityDestination))
        if isinstance(meta_message.destination, AddressDestination):
            destination_impl = meta_message.destination.implement(("", 0))
        elif isinstance(meta_message.destination, MemberDestination):
            destination_impl = meta_message.destination.implement(self._community.my_member)
        elif isinstance(meta_message.destination, SubjectiveDestination):
            destination_impl = self._decode_subjective_destination(meta_message, authentication_impl)
        elif isinstance(meta_message.destination, SimilarityDestination):
            destination_impl = self._decode_similarity_destination(meta_message, authentication_impl)
        else:
            destination_impl = meta_message.destination.implement()

        # distribution
        assert type(meta_message.distribution) in self._decode_distribution_map, type(meta_message.distribution)
        offset, distribution_impl = self._decode_distribution_map[type(meta_message.distribution)](offset, data, meta_message)

        # payload
        try:
            offset, payload = decode_payload_func(meta_message, offset, data[:first_signature_offset])
        except:
            if __debug__: dprint(decode_payload_func)
            raise

        if __debug__:
            from payload import Payload
        assert isinstance(payload, Payload.Implementation), type(payload)
        assert isinstance(offset, (int, long))

        return meta_message.implement(authentication_impl, distribution_impl, destination_impl, payload, conversion=self, packet=data)

    def decode_message(self, data):
        """
        Decode a binary string into a Message structure.
        """
        return self._decode_message(data, True)

class DefaultConversion(BinaryConversion):
    """
    This conversion class is initially used to encode some Dispersy
    specific messages during the creation of a new Community
    (authorizing the initial member).  Afterwards it is usually
    replaced by a Community specific conversion that also supplies
    payload conversion for the Community specific messages.
    """
    def __init__(self, community):
        super(DefaultConversion, self).__init__(community, "\x00\x00")
