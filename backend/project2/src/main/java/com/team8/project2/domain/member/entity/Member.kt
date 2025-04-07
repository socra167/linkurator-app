package com.team8.project2.domain.member.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime

@Entity
@EntityListeners(
    AuditingEntityListener::class
)
class Member() {
    @Id // PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private var id: Long? = null // long -> null X, Long -> null O

    @CreatedDate
    private val createdDate: LocalDateTime? = null

    @LastModifiedDate
    private val modifiedDate: LocalDateTime? = null

    @Column(length = 100, unique = true)
    lateinit var memberId: String

    @Column(length = 100, unique = true, nullable = true)
    private var username: String? = null

    @Column(nullable = false)
    private var password: String? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) //@Builder.Default
    var role = RoleEnum.MEMBER

    @Column
    var profileImage: String? = null

    @Column
    var email: String? = null

    @Column
    var introduce: String? = null

    constructor(
        memberId: String,
        password: String?,
        roleEnum: RoleEnum?,
        email: String?,
        profileImage: String?,
        introduce: String?
    ) : this() {
        this.memberId = memberId
        this.password = password
        this.role = roleEnum ?: RoleEnum.MEMBER
        this.email = email
        this.profileImage = profileImage
        this.introduce = introduce
    }

    constructor(id: Long, memberId: String) : this() {
        this.id = id
        this.memberId = memberId
    }

    constructor(
        memberId: String,
        username: String?,
        password: String?,
        roleEnum: RoleEnum?,
        profileImage: String?,
        email: String?,
        introduce: String?
    ) : this() {
        this.memberId = memberId
        this.username = username
        this.password = password
        this.role = roleEnum ?: RoleEnum.MEMBER
        this.profileImage = profileImage
        this.email = email
        this.introduce = introduce
    }


    val isAdmin: Boolean
        get() = this.role == RoleEnum.ADMIN
    val isMember: Boolean
        get() = this.role == RoleEnum.MEMBER

    val authorities: Collection<GrantedAuthority?>
        get() = memberAuthoritesAsString
            .stream()
            .map { role: String? ->
                SimpleGrantedAuthority(
                    role
                )
            }
            .toList()

    val memberAuthoritesAsString: List<String>
        get() {
            val authorities: MutableList<String> = ArrayList()

            if (isAdmin) {
                authorities.add("ROLE_ADMIN")
            }

            return authorities
        }
}