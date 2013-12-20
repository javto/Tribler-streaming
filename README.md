## Bittorrent video-on-demand streaming for Android
### Tribler Streaming

Goal: video-on-demand for mobile devices without _any_ servers

Solution: Combining Libtorrent and VLC into a single .APK

Clone the repository to get the APK in Tribler-streaming/Work/APK/

_Status: operational on Nexus 10 device_

Limitations and ToDo: improve seek behavior, may crash when swarm download to slow (buffer underrun)

##Project in detail
[full version in this 83-page report](https://github.com/javto/Tribler-streaming/blob/master/Reports/Final%20Report/finalreport.main.pdf?raw=true)

### VLC

The first step into building a streaming application, is checking if VLC can
be compiled for Android and if its source code can be modified. Otherwise, a
different video decoding framework had to be included in the prototype. VLC
was already available for Android, as well as its source code. Later on, it is
necessary to include VLC in the prototype (see Section 16.4). To do this, the
source code is needed as well as a method to compile it into an application.
VLC was compiled by following the guide in Appendix B. This guide was made
by collecting information from different resources over the Internet, including
the VLC wiki and several forums. The guide is also made available on Github
(see Section 4.3). Now, VLC has been tested to work on the target device and
can be modified to meet the needs of the prototype.

### Libtorrent

To build the Libtorrent libraries for Android, the Team first tested its func-
tionality on Linux. It was easily compiled for Linux in which the Team ran a
client test to see if a torrent file could be downloaded. Android however, has a
different compiler, which comes with the Android NDK. To compile Libtorrent
with this compiler was not without errors however. This was due to Libtorrent’s
dependency on Boost.

### Boost

This dependency meant that the Boost libraries had to be build for Android as
well. Boost is a popular set of C++ libraries, which include functions such as
image processing, regular expressions and multi-threading. In the end, a github
repository was used which builds Boost for Android after calling a compile
script, in which also the version of Boost can be specified.

### RuTracker

After the Boost libraries were put in place, the Libtorrent library could still not
be compiled with the compiler from the Android NDK. A lot of experimentation
was done with different versions of Boost, different compile options and different
versions of Libtorrent, but without satisfactory result. Then Egbert Bouman
from within the Tribler team came with a tip to look into the simple torrent
client by the name of: RuTracker , short for Russian tracker. This application
for Android was open source and used Libtorrent for its download functionality.
The Libtorrent libraries could then be build with the ndk-build command. The
few errors still remaining were quickly resolved by changing one of Libtorrent’s
configuration options (see Appendix C). The guide on building Libtorrent and
Boost is also made available on Github (see Section 4.3). The only drawback
of using RuTracker’s method is that it can not build the Libtorrent libraries for
the ‘Mips’ and ‘x86’ architectures, meaning that some Intel and Mips tablets
are not supported.
After building the libraries, the Team extracted these and put them in a separate
client test project.
The application could, at this stage, download a torrent and send the down-
loaded file to the previously build, and separately installed, VLC for playback.
The torrent file itself must first be downloaded to the device so it can be se-
lected in the application. At this stage, this works by using a separately installed
file-browser, such as ASTRO .


### Combining VLC and Libtorrent

The Team wanted to deliver the streaming application with VLC built-in. This
way, the user can play a video without having to worry about installing the right
media framework. To achieve this, the Team tried to let VLC act like a library
from which the Libtorrent client application could call functions for playback.
However, VLC for Android wasn’t build with this purpose in mind, as the
team discovered after some experimentation. The Team fused the two projects
together by putting the source of the Libtorrent client application together with
VLC in one project. The Team now had one .apk file, which can be installed.
At this stage, it first downloads the file and then calls the VLC part of the
prototype to play the media file.

### Streaming

The next part to implement is to download the media file while VLC plays it at
the same time. By making two threads, one for downloading and one for VLC,
the application could download while the user watches the video. To make this a
smooth experience, the Team implemented a buffer, which filled until the buffer
was large enough, so that the user could continuously watch the video. This is
based on the average speed and the file-size of the video to be streamed. In the
end, the download algorithm wasn’t implemented as described in Section 8.2.4.
The prototype sets Libtorrent to download pieces in sequential mode as advised
by the Tribler team.

============

Thnx to http://rutracker.org/forum/index.php for their running Libtorrent Android example code!

