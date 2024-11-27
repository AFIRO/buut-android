package rise.tiao1.buut.domain.user

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.data.remote.user.dto.RoleDTO

@ExperimentalCoroutinesApi
class RoleKtTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun whenToRoleDtoIsCalled_returnsCorrectDto() = scope.runTest{
        val given = getRole()
        val expected = getRoleDTO()
        val result = given.toRoleDTO()

        assert(result.equals(expected))
    }
}

fun getRole() : Role {
    return Role(
        name = "Tester"
    )
}

fun getRoleDTO() : RoleDTO {
    return RoleDTO(
        name = "Tester"
    )
}