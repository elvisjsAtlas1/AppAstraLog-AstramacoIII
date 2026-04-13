package com.example.astralog.ui.screens.pedidos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astralog.data.remote.response.PedidoResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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

    when {
        state.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
            }
        }

        state.error != null -> {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = state.error ?: "Error",
                    color = MaterialTheme.colorScheme.error
                )

                Button(
                    onClick = onBack,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Volver")
                }
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Mis pedidos",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                if (state.pedidos.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("No tienes pedidos asignados aún")
                            }
                        }
                    }
                }

                items(state.pedidos, key = { it.id }) { pedido ->
                    PedidoItem(
                        pedido = pedido,
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

                item {
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver al perfil")
                    }
                }
            }
        }
    }
}

@Composable
private fun PedidoItem(
    pedido: PedidoResponse,
    onAbrirMapa: (String) -> Unit,
    onLlamar: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Pedido #${pedido.id}",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Cliente: ${pedido.clienteNombre}")
            Text(
                text = "Teléfono: ${pedido.clienteTelefono}",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onLlamar(pedido.clienteTelefono)
                }
            )

            Text(
                text = "Dirección: ${pedido.direccionEnvio}",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onAbrirMapa(pedido.direccionEnvio)
                }
            )

            Text("Material: ${pedido.material}")
            Text("Cantidad: ${pedido.cantidad}")
            Text("Total: ${pedido.montoTotal}")
            Text("Adelanto: ${pedido.adelanto}")
            Text("Piso: ${pedido.piso}")
            Text("Hora: ${pedido.horaEnvio}")
            Text("Código: ${pedido.codigoVerificacion}")
            Text("Estado: ${pedido.estado}")
        }
    }
}