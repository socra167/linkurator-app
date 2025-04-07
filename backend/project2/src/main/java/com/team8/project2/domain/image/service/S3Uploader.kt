package com.team8.project2.domain.image.service

import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.io.IOException
import java.time.Duration
import java.util.*

@Service
@RequiredArgsConstructor
class S3Uploader {
    private val s3Client: S3Client? = null
    private val s3Presigner: S3Presigner? = null

    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucketName: String? = null

    @Transactional
    fun deleteFile(imageName: String?) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(imageName)
            .build()

        s3Client!!.deleteObject(deleteObjectRequest)
    }

    @Transactional
    @Throws(IOException::class)
    fun uploadFile(file: MultipartFile): String {
        val fileName = UUID.randomUUID().toString() + "_" + file.originalFilename

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(file.contentType)
            .build()

        s3Client!!.putObject(putObjectRequest, RequestBody.fromInputStream(file.inputStream, file.size))

        return fileName
    }

    private fun getPresignedUrl(fileName: String): String {
        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10)) // 유효 시간 설정
            .getObjectRequest { req: GetObjectRequest.Builder ->
                req.bucket(
                    bucketName
                ).key(fileName)
            }
            .build()

        val presignedRequest = s3Presigner!!.presignGetObject(presignRequest)
        return presignedRequest.url().toString()
    }

    val baseUrl: String
        get() = "https://$bucketName.s3.amazonaws.com/"
}