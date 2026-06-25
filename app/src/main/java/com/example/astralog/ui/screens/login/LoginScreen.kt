package com.example.astralog.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// 🔮 Paleta de Colores Espaciales Unificada con el Web Login
private val SpaceBackground = Color(0xFF090D16)
private val CardBackground = Color(0xFF0F172A).copy(alpha = 0.75f)
private val NeonBlue = Color(0xFF3B82F6)
private val NeonPurple = Color(0xFF7C3AED)
private val TextMuted = Color(0xFF94A3B8)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    // Contenedor principal con fondo espacial profundo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBackground),
        contentAlignment = Alignment.Center
    ) {
        // Efecto aura neón de fondo (Simula el desenfoque del login)
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .blur(80.dp)
                .background(Brush.radialGradient(listOf(NeonBlue.copy(alpha = 0.2f), Color.Transparent)))
        )

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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LogoSection()

        Spacer(modifier = Modifier.height(36.dp))

        // 🔥 TARJETA DE CRISTAL (GLASSMORPHISM BOX)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            // Animación fluida para errores (Evita alertas bruscas nativas)
            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ErrorMessage(error = state.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LoginButton(
                isLoading = state.isLoading,
                onClick = onLoginClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        RoleHint()
    }
}

@Composable
private fun LogoSection() {
    Box(
        modifier = Modifier
            .size(86.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors = listOf(NeonBlue, NeonPurple))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🚛",
            fontSize = 38.sp
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Astralog",
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        letterSpacing = 0.5.sp
    )

    Text(
        text = "Sistema de Gestión de Cargas",
        fontSize = 14.sp,
        color = TextMuted,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp)
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
                tint = NeonBlue
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF0F172A),
            unfocusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.5f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = NeonBlue,
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            focusedLabelColor = NeonBlue,
            unfocusedLabelColor = TextMuted
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
                tint = NeonBlue
            )
        },
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Ver contraseña",
                    tint = TextMuted
                )
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF0F172A),
            unfocusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.5f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = NeonBlue,
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            focusedLabelColor = NeonBlue,
            unfocusedLabelColor = TextMuted
        )
    )
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
            .height(52.dp)
            .background(
                brush = Brush.linearGradient(colors = listOf(NeonBlue, NeonPurple)),
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, // El degradado se maneja en el background modifier
            disabledContainerColor = Color.Transparent
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = "Ingresar al Sistema",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ErrorMessage(error: String?) {
    error?.let {
        Text(
            text = "⚠️ $it",
            color = MaterialTheme.colorScheme.error,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RoleHint() {
    Text(
        text = "Acceso restringido para personal: TRANSPORTISTA",
        fontSize = 12.sp,
        color = TextMuted,
        textAlign = TextAlign.Center
    )
}