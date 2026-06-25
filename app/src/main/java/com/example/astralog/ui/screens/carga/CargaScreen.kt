package com.example.astralog.ui.screens.carga

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astralog.data.remote.response.CargaResponse
import com.example.astralog.data.remote.response.TransportistaResponse

// 🌌 Paleta de Colores Dark Glassmorphism Unificada
private val SpaceBackground = Color(0xFF090D16)
private val CardBackground = Color(0xFF0F172A).copy(alpha = 0.75f)
private val NeonBlue = Color(0xFF3B82F6)
private val NeonPurple = Color(0xFF7C3AED)
private val TextMuted = Color(0xFF94A3B8)
private val GlassBorder = Color.White.copy(alpha = 0.12f)

// Estados Satinados
private val SuccessGreen = Color(0xFF10B981)
private val SuccessBg = Color(0xFF10B981).copy(alpha = 0.15f)
private val ErrorRed = Color(0xFFEF4444)
private val ErrorBg = Color(0xFFEF4444).copy(alpha = 0.15f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaScreen(
    onBack: () -> Unit,
    viewModel: CargaViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    Scaffold(
        containerColor = SpaceBackground,
        topBar = {
            TopAppBar(
                title = { Text("Mi Carga", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpaceBackground,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Aura ambiental trasera
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.TopStart)
                    .offset(x = (-40).dp, y = (-40).dp)
                    .blur(100.dp)
                    .background(Brush.radialGradient(listOf(NeonBlue.copy(alpha = 0.15f), Color.Transparent)))
            )

            if (state.isLoading) {
                LoadingView()
            } else {
                CargaContentView(
                    state = state,
                    onTipoMaterialChange = viewModel::onTipoMaterialChange,
                    onCantidadChange = viewModel::onCantidadChange,
                    onCantidadAumentarChange = viewModel::onCantidadAumentarChange,
                    onSubirCarga = viewModel::subirCargaActual,
                    onAumentarCarga = viewModel::aumentarCargaActual
                )
            }
        }
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = NeonBlue)
    }
}

@Composable
private fun CargaContentView(
    state: CargaUiState,
    onTipoMaterialChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onCantidadAumentarChange: (String) -> Unit,
    onSubirCarga: () -> Unit,
    onAumentarCarga: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(modifier = Modifier.height(2.dp))

        TransportistaMiniHeader(transportista = state.transportista)

        FeedbackSection(error = state.error, success = state.success)

        CargaActualSection(carga = state.carga)

        SubirCargaSection(
            tipoMaterialSeleccionado = state.tipoMaterialSeleccionado,
            amountText = state.cantidadTexto,
            onTipoMaterialChange = onTipoMaterialChange,
            onCantidadChange = onCantidadChange,
            onSubirCarga = onSubirCarga
        )

        AumentarCargaSection(
            cantidadAumentarTexto = state.cantidadAumentarTexto,
            onCantidadAumentarChange = onCantidadAumentarChange,
            onAumentarCarga = onAumentarCarga
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TransportistaMiniHeader(
    transportista: TransportistaResponse?,
    modifier: Modifier = Modifier
) {
    transportista?.let {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(NeonBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = NeonBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "${it.nombre} ${it.apellidos}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${it.tipoTransporte.uppercase()} • Placa: ${it.placa}",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackSection(
    error: String?,
    success: String?,
    modifier: Modifier = Modifier
) {
    if (error != null || success != null) {
        val isError = error != null
        val backgroundColor = if (isError) ErrorBg else SuccessBg
        val contentColor = if (isError) ErrorRed else SuccessGreen

        Surface(
            modifier = modifier.fillMaxWidth(),
            color = backgroundColor,
            shape = RoundedCornerShape(14.dp),
            // 🔥 CORRECCIÓN AQUÍ: Usamos BorderStroke nativo de Compose
            border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isError) Icons.Default.Info else Icons.Default.AssignmentTurnedIn,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = error ?: success ?: "",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CargaActualSection(
    carga: CargaResponse?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackground)
            .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
            .padding(22.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado de tu Carga",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                val (badgeText, badgeBg, badgeValueColor) = if (carga != null) {
                    Triple("CON CARGA", SuccessBg, SuccessGreen)
                } else {
                    Triple("VACÍO", Color.White.copy(alpha = 0.05f), TextMuted)
                }

                // Cambia el Surface del Badge por este:
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp),
                    // 🔥 CORRECCIÓN AQUÍ: Usamos BorderStroke nativo de Compose
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeValueColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = badgeText,
                        color = badgeValueColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (carga != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Material", fontSize = 12.sp, color = TextMuted)
                        Text(carga.tipoMaterial, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Cantidad Disponible", fontSize = 12.sp, color = TextMuted)
                        Text(
                            text = "${carga.cantidadDisponible} ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = NeonBlue
                        )
                    }
                }
            } else {
                Text(
                    text = "Aún no tienes carga registrada en sistema para iniciar tu jornada.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun SubirCargaSection(
    tipoMaterialSeleccionado: String,
    amountText: String,
    onTipoMaterialChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onSubirCarga: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackground)
            .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
            .padding(22.dp)
    ) {
        Column {
            Text(
                text = "Nueva Carga / Reemplazar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Selecciona el tipo de material",
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            MaterialSelectorGroup(
                selectedValue = tipoMaterialSeleccionado,
                onValueChange = onTipoMaterialChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = onCantidadChange,
                label = { Text("Cantidad total disponible ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSubirCarga,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Brush.linearGradient(colors = listOf(NeonBlue, NeonPurple)), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent)
            ) {
                Text("Registrar Nueva Carga", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun MaterialSelectorGroup(
    selectedValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val opciones = remember { listOf("PANDERETA", "TECHO") }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        opciones.forEach { opcion ->
            val isSelected = selectedValue == opcion

            val backgroundColor = if (isSelected) NeonBlue.copy(alpha = 0.15f) else Color.Transparent
            val borderWith = if (isSelected) 2.dp else 1.dp
            val borderColor = if (isSelected) NeonBlue else Color.White.copy(alpha = 0.1f)
            val textColor = if (isSelected) Color.White else TextMuted
            val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(backgroundColor)
                    .border(borderWith, borderColor, RoundedCornerShape(14.dp))
                    .clickable { onValueChange(opcion) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opcion,
                    fontWeight = fontWeight,
                    color = textColor,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun AumentarCargaSection(
    cantidadAumentarTexto: String,
    onCantidadAumentarChange: (String) -> Unit,
    onAumentarCarga: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackground)
            .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
            .padding(22.dp)
    ) {
        Column {
            Text(
                text = "Aumentar Carga Actual",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Añade stock solo si coincide con el material que ya llevas.",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cantidadAumentarTexto,
                onValueChange = onCantidadAumentarChange,
                label = { Text("Cantidad a agregar ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedLabelColor = NeonPurple,
                    unfocusedLabelColor = TextMuted
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAumentarCarga,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Brush.linearGradient(colors = listOf(NeonPurple, NeonPurple.copy(alpha = 0.6f))), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent)
            ) {
                Text("Añadir a la Carga", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}