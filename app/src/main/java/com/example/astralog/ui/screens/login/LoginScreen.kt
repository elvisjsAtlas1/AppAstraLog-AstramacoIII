package com.example.astralog.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LoginContent(
            state = state,
            passwordVisible = passwordVisible,
            onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
            onUsernameChange = viewModel::onUsernameChange,
            onPasswordChange = viewModel::onPasswordChange,
            onLoginClick = { viewModel.login(onLoginSuccess) }
        )
    }
}

@Composable
private fun LoginContent(
    state: LoginUiState,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LogoSection()

            Spacer(modifier = Modifier.height(48.dp))

            UsernameTextField(
                value = state.username,
                onValueChange = onUsernameChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                isPasswordVisible = passwordVisible,
                onVisibilityToggle = onPasswordVisibilityToggle
            )

            Spacer(modifier = Modifier.height(24.dp))

            ErrorMessage(error = state.error)

            LoginButton(
                isLoading = state.isLoading,
                onClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleHint()
        }
    }
}

@Composable
private fun LogoSection() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "A",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Astralog",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Text(
        text = "Sistema de Gestión de Cargas",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun UsernameTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Usuario") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Usuario",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onVisibilityToggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Contraseña") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Contraseña",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            PasswordVisibilityIcon(
                isVisible = isPasswordVisible,
                onToggle = onVisibilityToggle
            )
        },
        visualTransformation = getPasswordVisualTransformation(isPasswordVisible),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun PasswordVisibilityIcon(
    isVisible: Boolean,
    onToggle: () -> Unit
) {
    val (icon, description) = getPasswordVisibilityIconAndDescription(isVisible)

    IconButton(onClick = onToggle) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        ),
        enabled = !isLoading
    ) {
        LoginButtonContent(isLoading = isLoading)
    }
}

@Composable
private fun LoginButtonContent(isLoading: Boolean) {
    when {
        isLoading -> LoadingIndicator()
        else -> LoginButtonText()
    }
}

@Composable
private fun LoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp),
        color = Color.White
    )
}

@Composable
private fun LoginButtonText() {
    Text(
        text = "Ingresar",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
private fun ErrorMessage(error: String?) {
    error?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun RoleHint() {
    Text(
        text = "Rol esperado: TRANSPORTISTA",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        textAlign = TextAlign.Center
    )
}

// Funciones puras (sin @Composable) para lógica de negocio
private fun getPasswordVisualTransformation(isVisible: Boolean): VisualTransformation {
    return if (isVisible) VisualTransformation.None else PasswordVisualTransformation()
}

private fun getPasswordVisibilityIconAndDescription(isVisible: Boolean): Pair<ImageVector, String> {
    return if (isVisible) {
        Pair(Icons.Default.VisibilityOff, "Ocultar contraseña")
    } else {
        Pair(Icons.Default.Visibility, "Mostrar contraseña")
    }
}