package com.team8.project2.domain.image.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class S3UploaderTest {
    @Mock
    private val s3Client: S3Client? = null

    @Mock
    private val multipartFile: MultipartFile? = null

    @InjectMocks
    private val s3Uploader: S3Uploader? = null

    @Test
    @DisplayName("S3 버킷에서 이미지를 삭제할 수 있다")
    fun deleteFile() {
        val fileName = "test-image.jpg"
        s3Uploader!!.deleteFile(fileName)
        Mockito.verify(s3Client)!!.deleteObject(
            ArgumentMatchers.any(
                DeleteObjectRequest::class.java
            )
        )
    }

    @Test
    @DisplayName("S3 버킷에 이미지를 업로드할 수 있다")
    @Throws(IOException::class)
    fun uploadFile() {
        val fileName = UUID.randomUUID().toString() + "_test-image.jpg"
        Mockito.`when`(multipartFile!!.originalFilename).thenReturn("test-image.jpg")
        Mockito.`when`(multipartFile.contentType).thenReturn("image/jpeg")
        Mockito.`when`(multipartFile.inputStream).thenReturn(ByteArrayInputStream(ByteArray(0)))
        Mockito.`when`(multipartFile.size).thenReturn(10L)

        val uploadedFileName = s3Uploader!!.uploadFile(multipartFile)
        Assertions.assertNotNull(uploadedFileName)
        Mockito.verify(s3Client)!!.putObject(
            ArgumentMatchers.any(
                PutObjectRequest::class.java
            ), ArgumentMatchers.any(
                RequestBody::class.java
            )
        )
    }
}