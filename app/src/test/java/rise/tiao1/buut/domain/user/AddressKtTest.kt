package rise.tiao1.buut.domain.user

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.data.remote.user.dto.AddressDTO
import rise.tiao1.buut.utils.StreetType

@ExperimentalCoroutinesApi
class AddressKtTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun toDtoIsCalled_returnsCorrectDto() = scope.runTest {
        val address = getAddress()
        val expected = getAddressDto()
        val result = address.toAddressDTO()
        assert(result.equals(expected))
    }

    fun getAddress(): Address {
        return Address(StreetType.AFRIKALAAN, "TestHuisnummer", "TestBox")
    }

    fun getAddressDto(): AddressDTO {
        return AddressDTO(StreetType.AFRIKALAAN, "TestHuisnummer", "TestBox")
    }

}