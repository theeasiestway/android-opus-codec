//
// Created by Loboda Alexey on 27.03.2020.
//

#ifndef ANDROIDCODECPACK_LOGGER_H
#define ANDROIDCODECPACK_LOGGER_H
#include <android/log.h>


#define LOGE(tag, ...) __android_log_print(ANDROID_LOG_ERROR,    tag, __VA_ARGS__)
#define LOGW(tag, ...) __android_log_print(ANDROID_LOG_WARN,     tag, __VA_ARGS__)
#define LOGI(tag, ...) __android_log_print(ANDROID_LOG_INFO,     tag, __VA_ARGS__)
#define LOGD(tag, ...) __android_log_print(ANDROID_LOG_DEBUG,    tag, __VA_ARGS__)

#endif //ANDROIDCODECPACK_LOGGER_H
