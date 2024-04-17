package app_shared.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import app_shared.domain.model.util.args.*
import com.hospital.systems.hospitalsystems.generated.resources.*
import org.jetbrains.compose.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun WelcomeScreen(appArgs: AppArgs) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(Res.drawable.app_icon),
            contentDescription = "App icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer, BlendMode.SrcAtop)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Welcome to Hospital Systems!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            Text(
                text = "You are logged as ",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = when (appArgs) {
                    AppArgs.CLIENT -> "Patient"
                    AppArgs.DOCTOR -> "Doctor"
                    AppArgs.ADMIN -> "Administrator"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}