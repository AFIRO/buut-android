package rise.tiao1.buut.presentation.components

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import rise.tiao1.buut.R
import rise.tiao1.buut.utils.NavigationKeys
import rise.tiao1.buut.utils.UiLayout
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_EXPANDED
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_MEDIUM
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_SMALL
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_EXPANDED
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_MEDIUM
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_SMALL

@Composable
fun Navigation(
    uiLayout: UiLayout,
    navigateTo: (String) -> Unit,
    currentPage: String,
    content: @Composable () -> Unit = {}
) {
    val connectivityManager =
        LocalContext.current.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val isNetworkAvailable = connectivityManager.activeNetwork != null

    val navItems = listOf(
        NavigationItemContent(
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            text = stringResource(R.string.home),
            pageName = NavigationKeys.Route.HOME,
            navigateTo = { navigateTo(NavigationKeys.Route.HOME) }
        ),
        NavigationItemContent(
            selectedIcon = Icons.Filled.AddCircle,
            unselectedIcon = Icons.Outlined.AddCircleOutline,
            text = stringResource(R.string.new_booking),
            pageName = NavigationKeys.Route.CREATE_BOOKING,
            navigateTo = { navigateTo(NavigationKeys.Route.CREATE_BOOKING) },
        ),
        NavigationItemContent(
            selectedIcon = Icons.AutoMirrored.Filled.Help,
            unselectedIcon = Icons.AutoMirrored.Outlined.Help,
            text = stringResource(R.string.profile_button),
            pageName = NavigationKeys.Route.PROFILE,
            navigateTo = { navigateTo(NavigationKeys.Route.PROFILE) }
        )
    )

    when (uiLayout) {
        PORTRAIT_SMALL, PORTRAIT_MEDIUM -> {
            NavigationBar (
                containerColor = MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.testTag(stringResource(R.string.navigation_bar))
            ) {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentPage == item.pageName,
                        onClick = {
                            item.navigateTo()
                        },
                        label = {
                            Text(text = item.text)
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentPage == item.pageName) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.text
                            )
                        },
                        enabled = !(item.text == LocalContext.current.getString(R.string.new_booking) && !isNetworkAvailable)
                    )
                }
            }
        }

        LANDSCAPE_SMALL, LANDSCAPE_MEDIUM -> {
            NavigationRail(
                modifier = Modifier.testTag(stringResource(R.string.navigation_rail))
            ) {
                navItems.forEach{item ->
                    NavigationRailItem(
                        selected = currentPage == item.pageName,
                        onClick = {
                            item.navigateTo()
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentPage == item.pageName) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.text
                            )
                        },
                        enabled = !(item.text == LocalContext.current.getString(R.string.new_booking) && !isNetworkAvailable)
                    )
                }
            }
        }

        PORTRAIT_EXPANDED, LANDSCAPE_EXPANDED -> {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerContent = {
                    ModalDrawerSheet(
                        Modifier.width(180.dp),
                        ) {
                        navItems.forEach { item ->
                            NavigationDrawerItem(
                                selected = currentPage == item.pageName,
                                label = { Text(text = item.text) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentPage == item.pageName) {
                                            item.selectedIcon
                                        } else item.unselectedIcon,
                                        contentDescription = item.text
                                    )
                                },
                                onClick = {
                                    item.navigateTo()
                                },
                            )
                        }
                    }
                },
                drawerState = drawerState,
                modifier = Modifier.testTag(stringResource(R.string.navigation_drawer))
            ) {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.menu_button)
                            )
                        }
                    },
                    content = { paddingValues ->
                        Box(Modifier.padding(paddingValues)) {
                            content()
                        }
                    })
            }
        }
    }

}

private data class NavigationItemContent(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val text: String,
    val pageName: String,
    val navigateTo: () -> Unit,

    )