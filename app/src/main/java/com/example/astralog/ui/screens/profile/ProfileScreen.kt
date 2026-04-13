package com.example.astralog.ui.screens.profile

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astralog.data.remote.response.DocumentoResponse

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
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Volver al login")
                }
            }
        }

        state.transportista != null -> {
            val t = state.transportista!!

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${t.nombre} ${t.apellidos}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text("DNI: ${t.dni}")
                            Text("Edad: ${t.edad}")
                            Text("Tipo transporte: ${t.tipoTransporte}")
                            Text("Placa: ${t.placa}")
                            Text("Vehículo: ${t.vehiculoInfo}")
                            Text("Capacidad: ${t.capacidad}")
                            Text("Estado: ${t.estado}")
                        }
                    }
                }

                item {
                    Button(
                        onClick = onOpenCarga,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Gestionar mi carga")
                    }
                }

                item {
                    Button(
                        onClick = onOpenPedidos,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver mis pedidos")
                    }
                }

                item {
                    Text(
                        text = "Documentos",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                items(t.documentos, key = { it.id }) { documento ->
                    DocumentoItem(documento)
                }

                item {
                    Button(
                        onClick = {
                            viewModel.logout()
                            onLogout()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentoItem(documento: DocumentoResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = documento.tipoDocumento,
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Valor: ${documento.valor}")
            Text("Fecha emisión: ${documento.fechaEmision ?: "No aplica"}")
            Text("Fecha vencimiento: ${documento.fechaVencimiento ?: "No aplica"}")
            Text("Activo: ${if (documento.activo) "Sí" else "No"}")
        }
    }
}