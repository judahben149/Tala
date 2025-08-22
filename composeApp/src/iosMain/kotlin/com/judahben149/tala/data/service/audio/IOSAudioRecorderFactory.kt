package com.judahben149.tala.data.service.audio

class IOSAudioRecorderFactory : SpeechRecorderFactory {
    override fun createRecorder(): SpeechRecorder = IOSAudioRecorder()
}