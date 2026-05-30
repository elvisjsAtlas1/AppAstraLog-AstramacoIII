package com.example.astralog.ui.screens.pedidos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.runtime.remember

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
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos Asignados", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(
                    error = state.error ?: "Error desconocido",
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
    // 1. PRIMERO LA LÓGICA (Al inicio de la función, aprobado por SonarQube)
    val fechaIso = pedido.horaEnvio.toString()
    val (fechaFormateada, horaFormateada) = remember(fechaIso) {
        if (fechaIso.contains("T")) {
            val partes = fechaIso.split("T")
            val horaLimpia = partes[1].take(5) // Extrae solo "08:30"
            Pair(partes[0], horaLimpia)
        } else {
            Pair(fechaIso, "--:--")
        }
    }

    // 2. LUEGO LA UI (El árbol de maquetación limpio)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Cabecera del pedido (ID y Estado)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalMall,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pedido #${pedido.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                PedidoStatusBadge(estado = pedido.estado.toString())
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // Datos del cliente
            Text(
                text = "CLIENTE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Text(
                text = pedido.clienteNombre.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción directa estilo Rappi (Dirección y Llamar)
            ActionRowItem(
                label = "Dirección de entrega",
                value = pedido.direccionEnvio.toString(),
                icon = Icons.Default.Map,
                onClick = { onAbrirMapa(pedido.direccionEnvio.toString()) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ActionRowItem(
                label = "Teléfono de contacto",
                value = pedido.clienteTelefono.toString(),
                icon = Icons.Default.Call,
                onClick = { onLlamar(pedido.clienteTelefono.toString()) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // --- DISEÑO ARMONIZADO EN BLOQUES (Corregido y ordenado) ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Fila Superior: Datos de la Carga
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailColumn(
                        label = "Material",
                        value = pedido.material.toString(),
                        modifier = Modifier.weight(1.2f)
                    )
                    DetailColumn(
                        label = "Cantidad",
                        value = pedido.cantidad.toString(),
                        isPrimary = true,
                        modifier = Modifier.weight(0.9f)
                    )
                    DetailColumn(
                        label = "Piso",
                        value = pedido.piso.toString(),
                        modifier = Modifier.weight(0.5f)
                    )
                }

                // Fila Inferior: Logística de Tiempos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailColumn(
                        label = "Fecha de Envío",
                        value = fechaFormateada,
                        modifier = Modifier.weight(1.2f)
                    )
                    DetailColumn(
                        label = "Hora Programada",
                        value = horaFormateada,
                        modifier = Modifier.weight(1.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // Precios y Código
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total a Cobrar", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(pedido.montoTotal.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (pedido.adelanto.toString().isNotBlank()) {
                        Text("Adelanto: ${pedido.adelanto}", fontSize = 11.sp, color = Color(0xFF2E7D32))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Token de Entrega", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(pedido.codigoVerificacion.toString(), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 2)
        }
    }
}

@Composable
private fun DetailColumn(label: String, value: String, isPrimary: Boolean = false, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PedidoStatusBadge(estado: String, modifier: Modifier = Modifier) {
    val isEntregado = estado.equals("entregado", ignoreCase = true)
    val badgeColor = if (isEntregado) Color(0xFF2E7D32) else Color(0xFFE65100)
    val backgroundColor = if (isEntregado) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = estado.uppercase(),
            color = badgeColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(error: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Volver") }
    }
}

@Composable
private fun EmptyPedidosView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No tienes pedidos asignados aún", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}