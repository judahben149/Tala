package com.judahben149.tala.domain.models.exception.recording

sealed class AudioRecordingException(message: String, cause: Throwable? = null) : Exception(message, cause)

class AudioPermissionException(message: String = "Audio recording permission not granted") : AudioRecordingException(message)

class AudioConfigurationException(message: String) : AudioRecordingException(message)

class RecordingStateException(message: String) : AudioRecordingException(message)

class AudioHardwareException(message: String, cause: Throwable? = null) : AudioRecordingException(message, cause)

class RecordingNotStartedException(message: String) : AudioRecordingException(message)

class AlreadyRecordingException(message: String) : AudioRecordingException(message)