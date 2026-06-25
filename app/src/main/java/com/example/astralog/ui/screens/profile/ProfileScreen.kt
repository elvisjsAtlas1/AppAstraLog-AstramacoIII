package com.example.astralog.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astralog.data.remote.response.DocumentoResponse
import com.example.astralog.data.remote.response.TransportistaResponse

// 🌌 Paleta de Colores Dark Glassmorphism Unificada
private val SpaceBackground = Color(0xFF090D16)
private val CardBackground = Color(0xFF0F172A).copy(alpha = 0.75f)
private val NeonBlue = Color(0xFF3B82F6)
private val NeonPurple = Color(0xFF7C3AED)
private val TextMuted = Color(0xFF94A3B8)
private val GlassBorder = Color.White.copy(alpha = 0.12f)

// Colores de Estados Semánticos Satinados
private val SuccessGreen = Color(0xFF10B981)
private val SuccessBg = Color(0xFF10B981).copy(alpha = 0.15f)
private val ErrorRed = Color(0xFFEF4444)
private val ErrorBg = Color(0xFFEF4444).copy(alpha = 0.15f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onOpenCarga: () -> Unit,
    onOpenPedidos: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        containerColor = SpaceBackground, // Fondo oscuro profundo base
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White) },
                actions = {
                    IconButton(onClick = { viewModel.logout(); onLogout() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = ErrorRed
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
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Aura de neón difuminada de fondo ambiental
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-60).dp, y = 60.dp)
                    .blur(90.dp)
                    .background(Brush.radialGradient(listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent)))
            )

            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(
                    error = state.error,
                    onRetry = { viewModel.loadProfile() },
                    onLogout = { viewModel.logout(); onLogout() }
                )
                state.transportista != null -> ProfileContentView(
                    transportista = state.transportista!!,
                    onOpenCarga = onOpenCarga,
                    onOpenPedidos = onOpenPedidos
                )
            }
        }
    }
}

@Composable
private fun ProfileContentView(
    transportista: TransportistaResponse,
    onOpenCarga: () -> Unit,
    onOpenPedidos: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // 1. Tarjeta de Encabezado estilo Glassmorphism
        item {
            RappiProfileHeader(transportista = transportista)
        }

        // 2. Operaciones con Mosaicos Degradados
        item {
            Column {
                Text(
                    text = "Operaciones del Conductor",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    GridActionButton(
                        text = "Gestionar Carga",
                        subtext = "Inventario y pesos",
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        gradientColors = listOf(NeonBlue, NeonBlue.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f),
                        onClick = onOpenCarga
                    )
                    GridActionButton(
                        text = "Ver Pedidos",
                        subtext = "Rutas asignadas",
                        icon = Icons.Default.LocalShipping,
                        gradientColors = listOf(NeonPurple, NeonPurple.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f),
                        onClick = onOpenPedidos
                    )
                }
            }
        }

        // 3. Panel de Documentos Integrados
        item {
            DocumentosSection(documentos = transportista.documentos)
        }
    }
}

@Composable
private fun RappiProfileHeader(transportista: TransportistaResponse) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar circular translúcido neón
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(NeonBlue.copy(alpha = 0.15f))
                        .border(1.dp, NeonBlue.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = NeonBlue
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = "${transportista.nombre} ${transportista.apellidos}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val isActivo = transportista.estado.equals("activo", ignoreCase = true)
                    StatusBadge(
                        text = transportista.estado.uppercase(),
                        isActive = isActivo
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(16.dp))

            // Datos técnicos en cuadrícula limpia con tipografía espacial blanca
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VehicleDataChip(label = "Placa", value = transportista.placa)
                VehicleDataChip(label = "Capacidad", value = "${transportista.capacidad} kg")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VehicleDataChip(label = "Vehículo", value = transportista.vehiculoInfo)
                VehicleDataChip(label = "DNI", value = transportista.dni)
            }
        }
    }
}

@Composable
private fun GridActionButton(
    text: String,
    subtext: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(115.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(colors = gradientColors))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = subtext,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun DocumentosSection(
    documentos: List<DocumentoResponse>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Documentación Obligatoria",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        if (documentos.isEmpty()) {
            EmptyDocumentosView()
        } else {
            DocumentosCardList(documentos = documentos)
        }
    }
}

@Composable
private fun DocumentosCardList(
    documentos: List<DocumentoResponse>,
    modifier: Modifier = Modifier
) {
    var expandedDocumentoId by remember { mutableStateOf<Long?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackground)
            .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
    ) {
        Column {
            documentos.forEachIndexed { index, documento ->
                val isExpanded = expandedDocumentoId == documento.id

                DocumentoItemRow(
                    documento = documento,
                    isExpanded = isExpanded,
                    onClick = {
                        expandedDocumentoId = if (isExpanded) null else documento.id
                    }
                )

                if (index < documentos.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.White.copy(alpha = 0.06f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentoItemRow(
    documento: DocumentoResponse,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nombreFormateado = remember(documento.tipoDocumento) {
        documento.tipoDocumento
            .replace("_", " ")
            .lowercase()
            .split(" ")
            .joinToString(" ") { palabra ->
                palabra.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }

    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 90f else 0f, label = "rotation")

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = NeonBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombreFormateado,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Vence: ${documento.fechaVencimiento ?: "Vigente (Fijo)"}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            StatusBadge(
                text = if (documento.activo) "Al día" else "Vencido",
                isActive = documento.activo
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted.copy(alpha = 0.5f),
                modifier = Modifier.rotate(rotationAngle)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RowExtraInfo(label = "Código / Registro de Documento", value = documento.valor)
                RowExtraInfo(label = "Fecha de Emisión del Permiso", value = documento.fechaEmision ?: "No registrada")
                RowExtraInfo(label = "Estado de Validación", value = if (documento.activo) "Aprobado / Vigente" else "No Vigente o Inactivo")
            }
        }
    }
}

@Composable
private fun RowExtraInfo(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
}

@Composable
private fun StatusBadge(text: String, isActive: Boolean) {
    Surface(
        color = if (isActive) SuccessBg else ErrorBg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = if (isActive) SuccessGreen else ErrorRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun VehicleDataChip(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$label: ", fontSize = 13.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = NeonBlue)
    }
}

@Composable
private fun ErrorView(error: String?, onRetry: () -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = error ?: "Error de comunicación con el servidor", color = ErrorRed, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
        ) {
            Text("Reintentar Conexión", color = Color.White)
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = onLogout) {
            Text("Volver al inicio", color = ErrorRed)
        }
    }
}

@Composable
private fun EmptyDocumentosView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No se detectaron documentos guardados", color = TextMuted)
    }
}