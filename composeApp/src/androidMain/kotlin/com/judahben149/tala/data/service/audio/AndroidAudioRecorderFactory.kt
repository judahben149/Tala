package com.judahben149.tala.data.service.audio

class AndroidAudioRecorderFactory : SpeechRecorderFactory {
    override fun createRecorder(): SpeechRecorder = AndroidAudioRecorder()
}