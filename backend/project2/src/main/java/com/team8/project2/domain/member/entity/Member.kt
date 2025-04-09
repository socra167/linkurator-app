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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private var _id: Long? = null // TODO: 추후에 코틀린 전환 과정에서 해결

    var id: Long
        get() = _id ?: 0
        set(value) {
            _id = value
        }
    /*@Id // 기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private var id: Long? = null

    //nullable인데 null 예외 처리가 되어있음
    @get:JvmName("getId") // Java에서 getId()로 보이게
    val idOrThrow: Long?
        get() = id ?: throw UninitializedPropertyAccessException("id is not initialized")*/

    @CreatedDate
    var createdDate: LocalDateTime? = null

    @LastModifiedDate
    var modifiedDate: LocalDateTime? = null

    @Column(length = 100, unique = true)
    private var memberId: String? = null

    @Column(nullable = false)
    private var password: String? = null

    @Column(length = 100, unique = true, nullable = true)
    private var username: String? = null

    //username null시 annoymous 할당

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) //@Builder.Default
    var role = RoleEnum.MEMBER

    @Column
    var profileImage: String? = null

    @Column
    var email: String? = null

    @Column
    var introduce: String? = null

    //생성자


    constructor(
        memberId: String,
        username: String?,
        password: String?,
        role: RoleEnum?,
        profileImage: String?,
        email: String?,
        introduce: String?
    ) : this() {
        this.memberId = memberId
        this.username = username
        this.password = password
        this.role = role ?: RoleEnum.MEMBER
        this.profileImage = profileImage
        this.email = email
        this.introduce = introduce
    }

    // Test 용 생성자
    constructor(id: Long, memberId: String) : this() {
        this.id = id
        this.memberId = memberId
        this.password = "blank"
        this.username = "default"
    }
    constructor(
        memberId: String,
        password: String?,
        role: RoleEnum,
        email: String?,
        profileImage: String?,
        introduce: String?
    ) : this() {
        this.memberId = memberId
        this.password = password
        this.role = role
        this.email = email
        this.profileImage = profileImage
        this.introduce = introduce
    }

    constructor(id: Long, username: String, email: String) : this() {
        this.id = id
        this.username = username
        this.email = email
        this.password = "blank"
        this.memberId = "0"
    }

    constructor(memberId: String) : this() {
        this.memberId = memberId
        this.password = "blank"
        this.username = "default"
        this.username = "default"
    }

    constructor(id: Long) : this() {
        this.id = id
        this.password = "blank"
        this.username = "default"
    }

    //getter
    fun getMemberId(): String {
        return memberId ?: throw UninitializedPropertyAccessException("memberId is not initialized")
    }
    fun getUsername(): String {
        return username ?: throw UninitializedPropertyAccessException("username is not initialized")
    }
    fun getPassword(): String {
        return password ?: throw UninitializedPropertyAccessException("password is not initialized")
    }
    // setter
    fun setUsername(newName: String) {
        this.username = newName
    }

    // 상태 체크
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