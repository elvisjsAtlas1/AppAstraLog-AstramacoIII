package com.example.astralog.ui.screens.pedidos

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Map
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astralog.data.remote.response.PedidoResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// 🌌 Paleta de Colores Dark Glassmorphism Unificada
private val SpaceBackground = Color(0xFF090D16)
private val CardBackground = Color(0xFF0F172A).copy(alpha = 0.75f)
private val NeonBlue = Color(0xFF3B82F6)
private val NeonPurple = Color(0xFF7C3AED)
private val TextMuted = Color(0xFF94A3B8)
private val GlassBorder = Color.White.copy(alpha = 0.12f)

// Estados Satinados Armonizados
private val SuccessGreen = Color(0xFF10B981)
private val SuccessBg = Color(0xFF10B981).copy(alpha = 0.15f)
private val WarningOrange = Color(0xFFF59E0B)
private val WarningBg = Color(0xFFF59E0B).copy(alpha = 0.15f)
private val ErrorRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    onBack: () -> Unit,
    viewModel: PedidosViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.cargarMisPedidos()
    }

    Scaffold(
        containerColor = SpaceBackground,
        topBar = {
            TopAppBar(
                title = { Text("Pedidos Asignados", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White) },
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
            // Aura ambiental neón trasera
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 80.dp, y = 80.dp)
                    .blur(110.dp)
                    .background(Brush.radialGradient(listOf(NeonBlue.copy(alpha = 0.12f), Color.Transparent)))
            )

            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(
                    error = state.error ?: "Error de comunicación",
                    onBack = onBack
                )
                else -> PedidosList(
                    pedidos = state.pedidos,
                    onAbrirMapa = { direccion ->
                        val encoded = URLEncoder.encode(direccion, StandardCharsets.UTF_8.toString())
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/search/?api=1&query=$encoded")
                        )
                        context.startActivity(intent)
                    },
                    onLlamar = { telefono ->
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$telefono")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun PedidosList(
    pedidos: List<PedidoResponse>,
    onAbrirMapa: (String) -> Unit,
    onLlamar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        if (pedidos.isEmpty()) {
            item { EmptyPedidosView() }
        } else {
            items(pedidos, key = { it.id }) { pedido ->
                PedidoItem(
                    pedido = pedido,
                    onAbrirMapa = onAbrirMapa,
                    onLlamar = onLlamar
                )
            }
        }
    }
}

@Composable
private fun PedidoItem(
    pedido: PedidoResponse,
    onAbrirMapa: (String) -> Unit,
    onLlamar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val fechaIso = pedido.horaEnvio.toString()
    val (fechaFormateada, horaFormateada) = remember(fechaIso) {
        if (fechaIso.contains("T")) {
            val partes = fechaIso.split("T")
            val horaLimpia = partes[1].take(5)
            Pair(partes[0], horaLimpia)
        } else {
            Pair(fechaIso, "--:--")
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            // Cabecera del pedido (ID y Estado)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NeonBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalMall,
                            contentDescription = null,
                            tint = NeonBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Pedido #${pedido.id}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                PedidoStatusBadge(estado = pedido.estado.toString())
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(14.dp))

            // Datos del cliente
            Text(
                text = "CLIENTE RECEPTOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Text(
                text = pedido.clienteNombre.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botones interactivos Glassmorphic
            ActionRowItem(
                label = "Dirección de entrega",
                value = pedido.direccionEnvio.toString(),
                icon = Icons.Default.Map,
                iconColor = NeonBlue,
                onClick = { onAbrirMapa(pedido.direccionEnvio.toString()) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            ActionRowItem(
                label = "Teléfono de contacto",
                value = pedido.clienteTelefono.toString(),
                icon = Icons.Default.Call,
                iconColor = SuccessGreen,
                onClick = { onLlamar(pedido.clienteTelefono.toString()) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(14.dp))

            // --- DISEÑO ARMONIZADO EN DETALLES ESPACIALES ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailColumn(label = "Material", value = pedido.material.toString(), modifier = Modifier.weight(1.2f))
                    DetailColumn(label = "Cantidad", value = "${pedido.cantidad} m³", isPrimary = true, modifier = Modifier.weight(0.9f))
                    DetailColumn(label = "Piso", value = pedido.piso.toString(), modifier = Modifier.weight(0.5f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailColumn(label = "Fecha de Envío", value = fechaFormateada, modifier = Modifier.weight(1.2f))
                    DetailColumn(label = "Hora Programada", value = "$horaFormateada Hrs", modifier = Modifier.weight(1.4f))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(14.dp))

            // Caja de Liquidación / Totales y Verificación
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total a Cobrar en Destino", fontSize = 11.sp, color = TextMuted)
                        Text("S/ ${pedido.montoTotal}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        if (pedido.adelanto.toString().isNotBlank() && pedido.adelanto.toString() != "0") {
                            Text("Adelantado: S/ ${pedido.adelanto}", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Token de Entrega", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = pedido.codigoVerificacion.toString(),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = NeonPurple,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionRowItem(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextMuted)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White, maxLines = 2)
        }
    }
}

@Composable
private fun DetailColumn(label: String, value: String, isPrimary: Boolean = false, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = TextMuted)
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPrimary) NeonBlue else Color.White
        )
    }
}

@Composable
private fun PedidoStatusBadge(estado: String, modifier: Modifier = Modifier) {
    val isEntregado = estado.equals("entregado", ignoreCase = true)
    val badgeColor = if (isEntregado) SuccessGreen else WarningOrange
    val backgroundColor = if (isEntregado) SuccessBg else WarningBg

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = estado.uppercase(),
            color = badgeColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = NeonBlue)
    }
}

@Composable
private fun ErrorView(error: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = error, color = ErrorRed, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
        ) {
            Text("Volver al Menú", color = Color.White)
        }
    }
}

@Composable
private fun EmptyPedidosView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No tienes pedidos asignados en este momento", color = TextMuted, textAlign = TextAlign.Center)
    }
}