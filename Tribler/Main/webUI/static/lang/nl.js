/*
Copyright (c) 2011 BitTorrent, Inc. All rights reserved.

Use of this source code is governed by a BSD-style that can be
found in the LICENSE file.
*/

var LANG_STR = [
	  "Torrentbestanden||*.torrent||Alle bestanden (*.*)||*.*||"
	, "OK"
	, "Annuleren"
	, "Toepassen"
	, "Ja"
	, "Nee"
	, "Sluiten"
	, "Voorkeuren"
	, "Taal"
	, "Taal:"
	, "Privacy"
	, "Automatisch op updates controleren"
	, "Bètaversies installeren"
	, "Anonieme informatie versturen bij updatecontrole"
	, "Tijdens downloaden"
	, ".!ut toevoegen aan onvoltooide bestanden"
	, "Schijfruimte altijd vooraf toewijzen"
	, "Stand-by voorkomen als torrents actief zijn"
	, "Weergave-opties"
	, "Bevestiging bij verwijderen torrents"
	, "Bevestiging bij verwijderen trackers"
	, "Bevestiging bij afsluiten"
	, "Duidelijke lijstachtergrond"
	, "Huidige snelheid in titelbalk weergeven"
	, "Snelheidslimieten in statusbalk tonen"
	, "Bij toevoegen torrents"
	, "Niet automatisch starten"
	, "Activeer programmavenster"
	, "Venster met bestanden van torrents weergeven"
	, "Acties voor dubbelklikken"
	, "Voor uploadende torrents:"
	, "Voor downloadende torrents:"
	, "Locatie gedownloade bestanden"
	, "Nieuwe downloads opslaan in:"
	, "Altijd dialoogscherm bij handmatig toevoegen"
	, "Verplaats voltooide downloads naar:"
	, "Aan torrents etiket toevoegen"
	, "Alleen uit standaard downloadmap verplaatsen"
	, ".torrents-locatie"
	, ".torrents opslaan in:"
	, "Verplaats .torrents na downloaden naar:"
	, "Automatisch .torrents inladen uit:"
	, "Verwijder ingeladen .torrents"
	, "Luisterpoort"
	, "Poort voor inkomende verbindingen:"
	, "Willekeurige prt"
	, "Altijd willekeurige poort kiezen"
	, "Poorttoewijzing met UPnP"
	, "Poorttoewijzing met NAT-PMP"
	, "Proxyserver"
	, "Type:"
	, "Proxy:"
	, "Poort:"
	, "Authenticatie"
	, "Gebruikersnaam:"
	, "Wachtwoord:"
	, "Hostnamen door proxy laten oplossen"
	, "Gebruik proxyserver voor P2P-verbindingen"
	, "Uitzondering in Windows-firewall toevoegen"
	, "Proxy-privacy"
	, "Alle locale DNS-lookups uitschakelen"
	, "Functies die privéinformatie lekken uitschakelen"
	, "Verbindingen die proxy niet ondersteunt uitschakelen"
	, "Algemene beperking uploadsnelheid"
	, "Max. uploadsnelheid (kB/s): [0: onbeperkt]"
	, "Automatisch"
	, "Andere uploadsnelheid bij geen downloads (kB/s):"
	, "Algemene beperking downloadsnelheid"
	, "Max. downloadsnelheid (kB/s): [0: onbeperkt]"
	, "Aantal verbindingen"
	, "Max. aantal verbindingen in totaal:"
	, "Max. aantal verbonden peers per torrent:"
	, "Aantal uploadslots per torrent:"
	, "Gebruik aanvullende uploadslots bij uploadsnelheid onder 90%"
	, "Algemene snelheidsbeperkingopties"
	, "Datalimiet toepassen op transportoverhead"
	, "Datalimiet toepassen op uTP-verbindingen"
	, "Basismogelijkheden BitTorrent"
	, "DHT-netwerk inschakelen"
	, "Vraag tracker om scrape-informatie"
	, "DHT bij nieuwe torrents inschakelen"
	, "Peeruitwisseling inschakelen"
	, "Lokale peer-ontdekking inschakelen"
	, "Beperk lokale peerbandbreedte"
	, "Geef deze IP/Hostnaam aan tracker door:"
	, "Protocolversleuteling"
	, "Uitgaand:"
	, "Inkomende legacy-verb. toestaan"
	, "Bandbreedtebeheer inschakelen [uTP]"
	, "UDP-trackerondersteuning inschakelen"
	, "Gebruik datalimiet"
	, "Instellingen datalimiet"
	, "Limiettype:"
	, "Beperking bandbreedte:"
	, "Periode (dagen):"
	, "Gebruiksgeschiedenis voor deze periode:"
	, "Geüpload:"
	, "Gedownload:"
	, "Geüpload + gedownload:"
	, "Periode:"
	, "Afgelopen %d dagen"
	, "Gesch. legen"
	, "Wachtrijopties"
	, "Max. aantal actieve torrents (up- of download):"
	, "Max. aantal actieve downloads:"
	, "Uploaden terwijl [standaardwaarden]"
	, "Minimum ratio (%):"
	, "Minimale seed-tijd (minuten):"
	, "Uploadtaken hebben hogere prioriteit dan downloadtaken"
	, "Wanneer µTorrent uploaddoel heeft bereikt"
	, "Beperk uploadsnelheid tot (kB/s): [0: stop]"
	, "Planner inschakelen"
	, "Plannertabel"
	, "Instellingen Planner"
	, "Beperkte uploadsnelheid (kB/s):"
	, "Beperkte downloadsnelheid (kB/s):"
	, "DHT uitschakelen bij afsluiten"
	, "WebUI inschakelen"
	, "Bevestiging"
	, "Naam:"
	, "Wachtwrd:"
	, "Gastaccount toestaan met gebruikersnaam:"
	, "Bereikbaarheid"
	, "Alternatieve luisterpoort (standaard is verbindingspoort):"
,"Toegang alleen toestaan vanaf deze IP’s (als u meerdere IP’s invoert, moet u deze scheiden door een komma):"
	, "Geavanceerde opties [WAARSCHUWING: niet wijzigen!]"
	, "Waarde:"
	, "Waar"
	, "Onwaar"
	, "Instellen"
	, "Popupsnelheidslijst [Scheid waarden met een komma]"
	, "Overschrijf automatische popupsnelheidslijst"
	, "Uploadsnelheden:"
	, "Downloadsnelheden:"
	, "Vaste etiketten [meerdere etiketten scheiden met |]"
	, "Zoekmachines [Formaat: naam|URL]"
	, "Basisopies buffer"
	, "De schijfbuffer wordt gebruikt om veelgebruikte gegevens op te slaan in het geheugen om het aantal schrijf- en leesbewerkingen op de harde schijf te verminderen. µTorrent beheert de buffer gewoonlijk zelf, maar je kunt dit ook zelf beheren met deze instellingen."
	, "Overschrijf automatische buffergrootte met eigen waarde (MB):"
	, "Verminder geheugengebruik indien buffer niet nodig is"
	, "Uitgebreide bufferopties"
	, "Buffering van schrijfbewerkingen inschakelen"
	, "Ongebruikte blokken elke 2 minuten wegschrijven"
	, "Voltooide delen onmiddellijk wegschrijven"
	, "Buffering van leesbewerkingen inschakelen"
	, "Buffering uitschakelen bij lage uploadsnelheid"
	, "Verwijder oude blokken uit buffer"
	, "Automatische buffergrootte bij"
	, "Windows-buffering van schijfschrijvingen uitschakelen"
	, "Windows-buffering van schijfuitlezingen uitschakelen"
	, "Programma uitvoeren"
	, "Dit programma uitvoeren als een torrent voltooid is:"
	, "Dit programma uitvoeren als torrent van status wijzigt:"
	, "Gebruik de volgende commando's:\r\n%F - Naam van gedownload bestand (voor torrents met 1 bestand)\r\n%D - Map waar bestanden worden opgeslagen\r\n%N - Titel van torrent\r\n%S - Torrentstatus\r\n%L - Etiket\r\n%T - Tracker\r\n%M - Statusmelding (zelfde als statuskolom)\r\n%I - HEX-gecodeerde info-hash\r\n\r\nStatus is een combinatie van:\r\ngestart = 1, controleren = 2, start-na-controle = 4,\r\ngecontroleerd = 8, fout = 16, gepauzeerd = 32, auto = 64, geladen = 128"
	, "Eigenschappen"
	, "Trackers (scheid rijen met lege regel)"
	, "Bandbreedte-opties"
	, "Max. uploadsnelheid (kB/s): [0: standaard]"
	, "Max. downloadsnelheid (kB/s): [0: standaard]"
	, "Aantal uploadposities: [0: standaard]"
	, "Seed terwijl"
	, "Overschrijf standaardinstellingen"
	, "Minimale ratio (%):"
	, "Minimale uploadtijd (minuten):"
	, "Andere instellingen"
	, "Aanvankelijk uploaden"
	, "DHT inschakelen"
	, "Peer-uitwisseling"
	, "Feed"
	, "Feed-adres:"
	, "Eigen alias:"
	, "Abonnement"
	, "Items niet automatisch downloaden"
	, "Automatisch items in feed downloaden"
	, "Slimme afleveringsfilter"
	, "Feeds||Favorieten|Geschiedenis||"
	, "All Feeds"
	, "Filterinstellingen"
	, "Naam:"
	, "Filter:"
	, "Niet:"
	, "Locatie:"
	, "Feed:"
	, "Kwaliteit:"
	, "Aflevering nr.: [bijv. 1x12-14]"
	, "Filteren volgens originele naam i.p.v. gedecodeerde naam"
	, "Downloads niet automatisch starten"
	, "Slimme afl.filter"
	, "Downloads hoogste prioriteit geven"
	, "Minimale interval:"
	, "Etiket voor nieuwe torrents:"
	, "RSS-feed toevoegen..."
	, "Feed wijzigen..."
	, "Feed uitschakelen"
	, "Feed inschakelen"
	, "Feed bijwerken"
	, "Feed verwijderen"
	, "Downloaden"
	, "Open koppeling in browser"
	, "Toevoegen aan favorieten"
	, "Toevoegen"
	, "Verwijderen"
	, "ALLES"
	, "(Alle)"
	, "(komt altijd overeen)||(komt eens overeen)||12 uur||1 dag||2 dagen||3 dagen||4 dagen||1 week||2 weken||3 weken||1 maand||"
	, "RSS-feed toevoegen"
	, "Wijzig RSS-feed"
	, "Remove RSS Feed(s)"
	, "Really delete the %d selected RSS Feeds?"
	, "RSS-feed \"%s\" echt verwijderen?"
	, "Volledige naam"
	, "Naam"
	, "Aflevering"
	, "Formaat"
	, "Codec"
	, "Datum"
	, "Feed"
	, "Bron-URL"
	, "IP"
	, "Poort"
	, "Cliënt"
	, "Vlaggen"
	, "%"
	, "Relevantie"
	, "Downsnelheid"
	, "Upsnelheid"
	, "Verz"
	, "Gewacht"
	, "Geüpload"
	, "Gedownload"
	, "Hashfout"
	, "Peer-dl."
	, "MaxUp"
	, "MaxDown"
	, "Wachtrij"
	, "Niet actief"
	, "Klaar"
	, "Eerste deel"
	, "Naam"
	, "Aant. delen"
	, "%"
	, "Prioriteit"
	, "Grootte"
	, "overslaan"
	, "laag"
	, "normaal"
	, "hoog"
	, "Gedownload:"
	, "Geüpload:"
	, "Seeds:"
	, "Resterend:"
	, "Downsnelheid:"
	, "Upsnelheid:"
	, "Peers:"
	, "Deelverh.:"
	, "Opslaan als:"
	, "Hash:"
	, "Algemeen"
	, "Overdracht"
	, "%d van %d verbonden (%d in zwerm)"
	, "D:%s U:%s - %s"
	, "Kopiëren"
	, "Herstel"
	, "Onbeperkt"
	, "IP's oplossen"
	, "Get File(s)"
	, "Niet downloaden"
	, "Hoge prioriteit"
	, "Lage prioriteit"
	, "Normale prioriteit"
	, "Kopieer Magnet-URI"
	, "Verwijder gegevens"
	, "Verwijder .torrent"
	, "Verwijder .torrent + gegevens"
	, "Forceer hercontrole"
	, "Forceer start"
	, "Etiket"
	, "Pauze"
	, "Eigenschappen"
	, "Verplaats omlaag"
	, "Verplaats omhoog"
	, "Verwijderen"
	, "Verwijder en"
	, "Start"
	, "Stop"
	, "Actief"
	, "Alles"
	, "Voltooid"
	, "Downloaden"
	, "Inactief"
	, "Geen etiket"
	, "||Besch.||Beschikbaarheid"
	, "Toegevoegd op"
	, "Voltooid op"
	, "Klaar"
	, "Gedownload"
	, "Downsnelheid"
	, "Resterend"
	, "Etiket"
	, "Naam"
	, "#"
	, "Peers"
	, "Resterend"
	, "Seeds"
	, "Uploaders/downloaders"
	, "Verhouding"
	, "Grootte"
	, "Bron-URL"
	, "Status"
	, "Geüpload"
	, "Upsnelheid"
	, "Weet je zeker dat je de %d geselecteerde torrents met bijbehorende gegevens wilt verwijderen?"
	, "Weet je zeker dat je de geselecteerde torrent met bijbehorende gegevens wilt verwijderen?"
	, "Weet je zeker dat je de %d geselecteerde torrents wilt verwijderen?"
	, "Weet je zeker dat je de geselecteerde torrent wilt verwijderen?"
	, "RSS-filter \"%S\" echt verwijderen?"
	, "Gecontroleerd %:.1d%%"
	, "Downloaden"
	, "Fout: %s"
	, "Gereed"
	, "Gepauzeerd"
	, "Wachtrij"
	, "Seed in wachtrij"
	, "Uploaden"
	, "Gestopt"
	, "Geef etiket"
	, "Geef het nieuwe etiket voor geselecteerde torrents op:"
	, "Nieuw etiket..."
	, "Verwijder etiket"
	, "Algemeen||Trackers||Peers||Delen||Bestanden||Snelheid||Log||"
	, "Torrent toevoegen"
	, "Voeg Torrent toe van URL"
	, "Pauze"
	, "Instellingen"
	, "Verplaats omlaag"
	, "Verplaats omhoog"
	, "Verwijder"
	, "RSS-downloader"
	, "Start"
	, "Stop"
	, "Bestand"
	, "Torrent toevoegen..."
	, "Torrent toevoegen via URL..."
	, "Opties"
	, "Voorkeuren"
	, "Toon categorielijst"
	, "Toon gedetailleerde info"
	, "Toon statusbalk"
	, "Toon werkbalk"
	, "Iconen op tabs"
	, "Help"
	, "µTorrent-webpagina"
	, "µTorrent-forums"
	, "Send WebUI Feedback"
	, "About µTorrent WebUI"
	, "Torrents"
	, "Pauzeer alle torrents"
	, "Hervat alle torrents"
	, "D: %s%z/s"
	, " L: %z/s"
	, " O: %z/s"
	, " T: %Z"
	, "U: %s%z/s"
	, "B"
	, "EB"
	, "GB"
	, "kB"
	, "MB"
	, "PB"
	, "TB"
	, "Geavanceerd"
	, "Bandbreedte"
	, "Verbinding"
	, "Schijfcache"
	, "Mappen"
	, "Algemeen"
	, "Planner"
	, "Wachtrij"
	, "Extra's UI"
	, "Uiterlijk"
	, "BitTorrent"
	, "Web UI"
	, "Datalimiet"
	, "Programma uitvoeren"
	, "Toon eigenschappen||Start/stop||Open map||Toon downloadbalk||"
	, "Uitgeschakeld||Ingeschakeld||Geforceerd||"
	, "(geen)||Socks4||Socks5||HTTPS|HTTPS||"
	, "Uploads||Downloads||Uploads + downloads||"
	, "MB||GB||"
	, "1||2||5||7||10||14||15||20||21||28||30||31||"
	, "Naam"
	, "Waarde"
	, "Ma||Di||Wo||Do||Vr||Za||Zo||"
	, "Maandag||Dinsdag||Woensdag||Donderdag||Vrijdag||Zaterdag||Zondag||"
	, "Snel"
	, "Volledige snelheid - Gebruik standaard bandbreedtelimieten"
	, "Beperkt"
	, "Beperkt - Gebruik bandbreedtelimieten volgens de planner"
	, "Slechts uploaden"
	, "Slechts uploaden - Alleen gegevens uploaden (incl. onvolledige)"
	, "Uitschakelen"
	, "Uitschakelen - Stopt alle torrents die niet geforceerd zijn"
	, "<= %d uren"
	, "(Negeer)"
	, "<= %d minuten"
	, "%dd %dh"
	, "%dh %dm"
	, "%dm %ds"
	, "%ds"
	, "%dw %dd"
	, "%dj %dw",
"More Action",
"Torrents",
		"Feeds",
		"Applicatie",
		"land",
		'Resterend', // i.e. how much time remaining
		"van", // i.e. 3 of 4 peers
		"/s", // "per second""
		"Plak de URL van een torrent of feed",
		"Home",
		"Afmelden",
		"Uploaden",
		"Alle feeds",
		"bitrate",
		"resolutie",
		"lengte",
		"streambaar",
		"type", // i.e. file extension
		"extern", // i.e. uTorrent remote
		"over",
		"sessies",
		"delen",
		"deze torrent delen",
		"link delen",
		"toevoegen",
		"afmelden",
		"aanmelden",
		"toegang overal",
		"aangemeld blijven",
		"downloaden",
		"De client is momenteel niet beschikbaar. Controleer of deze verbonden is met internet.",
		"Kan niet communiceren met de &micro;Torrent-client. Dit bericht verdwijnt automatisch zodra er weer een verbinding tot stand komt.",
		"Bestand openen",
		"Downloaden naar je computer",
		"Openen met VLC Media Player",
		"Acties",
		"seizoen" // i.e. of a TV show
];
