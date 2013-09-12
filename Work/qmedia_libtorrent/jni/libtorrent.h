#include <jni.h>
#include <android/log.h>
//-----------------------------------------------------------------------------
#ifndef __Included_Java_com_softwarrior_libtorrent_LibTorrent__
#define __Included_Java_com_softwarrior_libtorrent_LibTorrent__
//-----------------------------------------------------------------------------
#ifdef __cplusplus
extern "C" {
#endif
//-----------------------------------------------------------------------------
#define LOG_TAG "Softwarrior"
#define LOG_INFO(...) {__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__);}
#define LOG_ERR(...) {__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__);}
//-----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_SetSession
	(JNIEnv *env, jobject obj, jint ListenPort, jint UploadLimit, jint DownloadLimit);
//-----------------------------------------------------------------------------
//enum proxy_type
//{
//	0 - none, // a plain tcp socket is used, and the other settings are ignored.
//	1 - socks4, // socks4 server, requires username.
//	2 - socks5, // the hostname and port settings are used to connect to the proxy. No username or password is sent.
//	3 - socks5_pw, // the hostname and port are used to connect to the proxy. the username and password are used to authenticate with the proxy server.
//	4 - http, // the http proxy is only available for tracker and web seed traffic assumes anonymous access to proxy
//	5 - http_pw // http proxy with basic authentication uses username and password
//};
//-----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_SetProxy
	(JNIEnv *env, jobject obj, jint Type, jstring HostName, jint Port, jstring UserName, jstring Password);
//-----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_SetSessionOptions
	(JNIEnv *env, jobject obj, jboolean LSD, jboolean UPNP, jboolean NATPMP);
//-----------------------------------------------------------------------------
//StorageMode:
//0-storage_mode_allocate
//1-storage_mode_sparse
//2-storage_mode_compact
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_AddTorrent
	(JNIEnv *env, jobject obj, jstring SavePath, jstring TorrentFile, jint StorageMode);
//-----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_PauseSession
	(JNIEnv *, jobject);
//-----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_ResumeSession
	(JNIEnv *, jobject);
//-----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_AbortSession
	(JNIEnv *, jobject);
//-----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_RemoveTorrent
	(JNIEnv *env, jobject obj, jstring ContentFile);
//-----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_PauseTorrent
	(JNIEnv *env, jobject obj, jstring ContentFile);
//-----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_ResumeTorrent
	(JNIEnv *env, jobject obj, jstring ContentFile);
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL Java_com_softwarrior_libtorrent_LibTorrent_GetTorrentProgress
	(JNIEnv *env, jobject obj, jstring ContentFile);
//-----------------------------------------------------------------------------
JNIEXPORT jlong JNICALL Java_com_softwarrior_libtorrent_LibTorrent_GetTorrentProgressSize
	(JNIEnv *env, jobject obj, jstring ContentFile);
//-----------------------------------------------------------------------------
//enum state_t
//{
//	0 queued_for_checking,
//	1 checking_files,
//	2 downloading_metadata,
//	3 downloading,
//	4 finished,
//	5 seeding,
//	6 allocating,
//	7 checking_resume_data
//}
// + 8 paused
// + 9 queued
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL Java_com_softwarrior_libtorrent_LibTorrent_GetTorrentState
	(JNIEnv *, jobject, jstring ContentFile);
//-----------------------------------------------------------------------------
//static char const* state_str[] =
//{"checking (q)", "checking", "dl metadata", "downloading", "finished", "seeding", "allocating", "checking (r)"};
//-----------------------------------------------------------------------------
JNIEXPORT jstring JNICALL Java_com_softwarrior_libtorrent_LibTorrent_GetTorrentStatusText
	(JNIEnv *env, jobject obj, jstring ContentFile);
//-----------------------------------------------------------------------------
JNIEXPORT jstring JNICALL Java_com_softwarrior_libtorrent_LibTorrent_GetSessionStatusText
	(JNIEnv *env, jobject obj);
//-----------------------------------------------------------------------------
JNIEXPORT jstring JNICALL Java_com_softwarrior_libtorrent_LibTorrent_GetTorrentFiles
	(JNIEnv *env, jobject obj, jstring ContentFile);
//-----------------------------------------------------------------------------
//0 - piece is not downloaded at all
//1 - normal priority. Download order is dependent on availability
//2 - higher than normal priority. Pieces are preferred over pieces with the same availability, but not over pieces with lower availability
//3 - pieces are as likely to be picked as partial pieces.
//4 - pieces are preferred over partial pieces, but not over pieces with lower availability
//5 - currently the same as 4
//6 - piece is as likely to be picked as any piece with availability 1
//7 - maximum priority, availability is disregarded, the piece is preferred over any other piece with lower priority
JNIEXPORT jboolean JNICALL Java_com_softwarrior_libtorrent_LibTorrent_SetTorrentFilesPriority
	(JNIEnv *env, jobject obj, jbyteArray FilesPriority, jstring ContentFile);
//-----------------------------------------------------------------------------
JNIEXPORT jbyteArray JNICALL Java_com_softwarrior_libtorrent_LibTorrent_GetTorrentFilesPriority
	(JNIEnv *env, jobject obj, jstring ContentFile);
//-----------------------------------------------------------------------------
JNIEXPORT jstring JNICALL Java_com_softwarrior_libtorrent_LibTorrent_GetTorrentName
	(JNIEnv *env, jobject obj, jstring TorrentFile);
//-----------------------------------------------------------------------------
JNIEXPORT jlong JNICALL Java_com_softwarrior_libtorrent_LibTorrent_GetTorrentSize
	(JNIEnv *env, jobject obj, jstring TorrentFile);
//-----------------------------------------------------------------------------
#ifdef __cplusplus
}
#endif

#endif //__Included_Java_com_softwarrior_libtorrent_LibTorrent__
