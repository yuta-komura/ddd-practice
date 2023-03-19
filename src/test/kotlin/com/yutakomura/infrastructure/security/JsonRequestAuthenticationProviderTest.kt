package com.yutakomura.infrastructure.security

import com.ninjasquad.springmockk.MockkBean
import com.yutakomura.domain.role.GivenRole
import com.yutakomura.domain.role.RoleRepository
import com.yutakomura.domain.role.Value
import com.yutakomura.domain.user.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
@AutoConfigureMockMvc
class JsonRequestAuthenticationProviderTest {

    lateinit var authProvider: JsonRequestAuthenticationProvider

    @MockkBean
    lateinit var userRepository: UserRepository

    @MockkBean
    lateinit var roleRepository: RoleRepository

    @MockkBean
    lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setup() {
        authProvider =
            JsonRequestAuthenticationProvider(userRepository, roleRepository, passwordEncoder)
    }

    @Test
    fun 正しいemailとpasswordで認証が通る場合() {
        val id = Id(1)
        val email = Email("test@example.com")
        val password = Password("testpassword")
        val encodedPassword = EncodedPassword.from(password)
        val uniqueUser = UniqueUser(id, email, encodedPassword)
        every { userRepository.selectByEmail(email) } returns uniqueUser
        every { passwordEncoder.matches(password.value, encodedPassword.value) } returns true
        val role = mockk<GivenRole>()
        every { role.value.value } returns "USER"
        every { roleRepository.selectByUserId(uniqueUser.id) } returns listOf(role)

        val result = authProvider.authenticate(
            UsernamePasswordAuthenticationToken(
                email.value,
                password.value
            )
        )

        assertThat(result.isAuthenticated).isTrue
        assertThat(result.principal).isInstanceOf(LoginUser::class.java)
        val loginUser = result.principal as LoginUser
        assertThat(loginUser.id).isEqualTo(uniqueUser.id.value)
        assertThat(loginUser.authorities).contains(SimpleGrantedAuthority("USER"))
        verify(exactly = 1) { userRepository.selectByEmail(email) }
        verify(exactly = 1) { passwordEncoder.matches(password.value, encodedPassword.value) }
        verify(exactly = 1) { roleRepository.selectByUserId(id) }
    }

    @Test
    fun `emailが間違っている、BadCredentialsExceptionをスローする`() {
        val email = Email("test@example.com")
        val password = Password("testpassword")
        every { userRepository.selectByEmail(email) } returns null

        assertThrows<BadCredentialsException> {
            authProvider.authenticate(
                UsernamePasswordAuthenticationToken(
                    email.value,
                    password.value
                )
            )
        }

        verify(exactly = 1) { userRepository.selectByEmail(email) }
    }

    @Test
    fun `パスワードが間違っている場合、BadCredentialsExceptionをスローする`() {
        val id = 1
        val email = "test@example.com"
        val password = "password"
        val encodedPassword = "encoded_password"
        val uniqueUser = UniqueUser(Id(id), Email(email), EncodedPassword(encodedPassword))
        val userRepository = mockk<UserRepository>()
        every { userRepository.selectByEmail(Email(email)) } returns uniqueUser
        val passwordEncoder = mockk<PasswordEncoder>()
        every { passwordEncoder.matches(password, encodedPassword) } returns false
        val roleRepository = mockk<RoleRepository>()
        val role = GivenRole(Id(id), Value("USER"))
        every { roleRepository.selectByUserId(Id(id)) } returns listOf(role)

        val provider =
            JsonRequestAuthenticationProvider(userRepository, roleRepository, passwordEncoder)

        // テスト実行
        assertThrows<BadCredentialsException> {
            provider.authenticate(UsernamePasswordAuthenticationToken(email, password))
        }
    }

    @Test
    fun `権限が見つからなかった場合、BadCredentialsExceptionをスローする`() {
        val id = Id(1)
        val email = Email("test@example.com")
        val password = Password("testpassword")
        val encodedPassword = EncodedPassword.from(password)
        val uniqueUser = UniqueUser(id, email, encodedPassword)
        every { userRepository.selectByEmail(email) } returns uniqueUser
        every { passwordEncoder.matches(password.value, encodedPassword.value) } returns true
        val roles: List<GivenRole> = emptyList()
        every { roleRepository.selectByUserId(uniqueUser.id) } returns roles

        assertThrows<BadCredentialsException> {
            authProvider.authenticate(
                UsernamePasswordAuthenticationToken(email.value, password.value)
            )
        }
        verify(exactly = 1) { userRepository.selectByEmail(email) }
        verify(exactly = 1) { passwordEncoder.matches(password.value, encodedPassword.value) }
        verify(exactly = 1) { roleRepository.selectByUserId(id) }
    }
}