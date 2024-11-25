package rise.tiao1.buut.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.booking.TimeSlot

@Composable
fun BookingConfirmationModal(
    timeSlot: TimeSlot?,
    error: String?,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit,
    onUpdateBooking: (String) -> Unit = {},
    idOfBookingToUpdate: String? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("bookingConfirmationModal"),
        title = {
            if (error.isNullOrBlank()) {
                Text(text = stringResource(R.string.confirm_booking_modal_header))
            }
        },
        text = {
            if (error.isNullOrBlank()) {
                Text(text = stringResource(R.string.confirm_booking_prompt) + " ${timeSlot?.slot}?")
            } else {
                ActionErrorContainer(error)
            }
        },

        confirmButton = {
            if (error.isNullOrBlank() && idOfBookingToUpdate == null) {
                Button(onClick = onConfirm) {
                    Text(text = stringResource(R.string.confirm))
                }
            }

            if (error.isNullOrBlank() && idOfBookingToUpdate != null) {
                Button(onClick = {onUpdateBooking(idOfBookingToUpdate)}) {
                    Text(text = stringResource(R.string.update))
                }
            }

        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}