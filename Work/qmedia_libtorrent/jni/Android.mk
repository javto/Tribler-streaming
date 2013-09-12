LOCAL_PATH := $(call my-dir)

#libiconv \
include $(CLEAR_VARS) \
\
LOCAL_MODULE := libiconv \
LOCAL_CFLAGS := -Wno-multichar \
	-D_ANDROID \
	-DLIBDIR="c" \
	-DBUILDING_LIBICONV \
	-DIN_LIBRARY \
\
LOCAL_C_INCLUDES := $(LOCAL_PATH)/libiconv/include \
	$(LOCAL_PATH)/libiconv/lib \
	$(LOCAL_PATH)/libiconv \
	$(LOCAL_PATH)/libiconv/libcharset/lib \
\
LOCAL_SRC_FILES := libiconv/lib/iconv.c \
     libiconv/libcharset/lib/localcharset.c \
     libiconv/lib/relocatable.c \
\
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libiconv/include \
LOCAL_EXPORT_LDLIBS := -lz \
\
include $(BUILD_STATIC_LIBRARY) \

include $(CLEAR_VARS) 

LOCAL_MODULE := libboost_filesystem 
LOCAL_SRC_FILES := libboost/libboost_filesystem-gcc-mt-s-1_45.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost
LOCAL_EXPORT_LDLIBS := -lz 

include $(PREBUILT_STATIC_LIBRARY) 

include $(CLEAR_VARS) 

LOCAL_MODULE := libboost_system
LOCAL_SRC_FILES := libboost/libboost_system-gcc-mt-s-1_45.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost
LOCAL_EXPORT_LDLIBS := -lz

include $(PREBUILT_STATIC_LIBRARY) 


include $(CLEAR_VARS) 

LOCAL_MODULE := libboost_thread
LOCAL_SRC_FILES := libboost/libboost_thread_pthread-gcc-mt-s-1_45.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost
LOCAL_EXPORT_LDLIBS := -lz

include $(PREBUILT_STATIC_LIBRARY) 


include $(CLEAR_VARS) 

LOCAL_MODULE := com_example_fibonacci_FibLib
LOCAL_CFLAGS := -DBOOST_NO_INTRINSIC_WCHAR_T \
	-DBOOST_FILESYSTEM_NARROW_ONLY \
	-DTORRENT_DISABLE_ENCRYPTION \
	-DTORRENT_DISABLE_GEO_IP

LOCAL_LDLIBS := -llog
LOCAL_STATIC_LIBRARIES := libboost_filesystem \
	libboost_system \
	libboost_thread 
#libiconv

LOCAL_SRC_FILES := fib.cpp
LOCAL_SRC_FILES += com_example_fibonacci_FibLib.cpp

#LOCAL_SRC_FILES += client_test.cpp
#LOCAL_SRC_FILES += libtorrent.cpp \
	libtorrent/alert.cpp \
	libtorrent/allocator.cpp \
	libtorrent/assert.cpp \
	libtorrent/broadcast_socket.cpp \
	libtorrent/bt_peer_connection.cpp \
	libtorrent/kademlia/closest_nodes.cpp \
	libtorrent/connection_queue.cpp \
	libtorrent/ConvertUTF.cpp \
	libtorrent/create_torrent.cpp \
	libtorrent/kademlia/dht_tracker.cpp \
	libtorrent/disk_buffer_holder.cpp \
	libtorrent/disk_io_thread.cpp \
	libtorrent/entry.cpp \
	libtorrent/enum_net.cpp \
	libtorrent/error_code.cpp \
	libtorrent/escape_string.cpp \
	libtorrent/file.cpp \
	libtorrent/file_pool.cpp \
	libtorrent/file_storage.cpp \
	libtorrent/kademlia/find_data.cpp \
	libtorrent/gzip.cpp \
	libtorrent/http_connection.cpp \
	libtorrent/http_parser.cpp \
	libtorrent/http_seed_connection.cpp \
	libtorrent/http_stream.cpp \
	libtorrent/http_tracker_connection.cpp \
	libtorrent/identify_client.cpp \
	libtorrent/instantiate_connection.cpp \
	libtorrent/ip_filter.cpp \
	libtorrent/lazy_bdecode.cpp \
	libtorrent/logger.cpp \
	libtorrent/lsd.cpp \
	libtorrent/lt_trackers.cpp \
	libtorrent/magnet_uri.cpp \
	libtorrent/metadata_transfer.cpp \
	libtorrent/natpmp.cpp \
	libtorrent/kademlia/node.cpp \
	libtorrent/kademlia/node_id.cpp \
	libtorrent/parse_url.cpp \
	libtorrent/pe_crypto.cpp \
	libtorrent/peer_connection.cpp \
	libtorrent/piece_picker.cpp \
	libtorrent/policy.cpp \
	libtorrent/kademlia/refresh.cpp \
	libtorrent/kademlia/routing_table.cpp \
	libtorrent/kademlia/rpc_manager.cpp \
	libtorrent/session.cpp \
	libtorrent/session_impl.cpp \
	libtorrent/sha1.cpp \
	libtorrent/smart_ban.cpp \
	libtorrent/socks5_stream.cpp \
	libtorrent/stat.cpp \
	libtorrent/storage.cpp \
	libtorrent/torrent.cpp \
	libtorrent/torrent_handle.cpp \
	libtorrent/torrent_info.cpp \
	libtorrent/tracker_manager.cpp \
	libtorrent/kademlia/traversal_algorithm.cpp \
	libtorrent/udp_socket.cpp \
	libtorrent/udp_tracker_connection.cpp \
	libtorrent/upnp.cpp \
	libtorrent/ut_metadata.cpp \
	libtorrent/ut_pex.cpp \
	libtorrent/web_peer_connection.cpp
	
include $(BUILD_SHARED_LIBRARY)
