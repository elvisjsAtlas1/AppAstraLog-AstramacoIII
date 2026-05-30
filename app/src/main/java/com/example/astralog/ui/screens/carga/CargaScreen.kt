package com.example.astralog.ui.screens.carga

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astralog.data.remote.response.CargaResponse
import com.example.astralog.data.remote.response.TransportistaResponse

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
        topBar = {
            TopAppBar(
                title = { Text("Mi Carga", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
        CircularProgressIndicator()
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        TransportistaMiniHeader(transportista = state.transportista)

        FeedbackSection(error = state.error, success = state.success)

        CargaActualSection(carga = state.carga)

        SubirCargaSection(
            tipoMaterialSeleccionado = state.tipoMaterialSeleccionado,
            cantidadTexto = state.cantidadTexto,
            onTipoMaterialChange = onTipoMaterialChange,
            onCantidadChange = onCantidadChange,
            onSubirCarga = onSubirCarga
        )

        AumentarCargaSection(
            cantidadAumentarTexto = state.cantidadAumentarTexto,
            onCantidadAumentarChange = onCantidadAumentarChange,
            onAumentarCarga = onAumentarCarga
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TransportistaMiniHeader(
    transportista: TransportistaResponse?,
    modifier: Modifier = Modifier
) {
    transportista?.let {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${it.nombre} ${it.apellidos}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${it.tipoTransporte} • Placa: ${it.placa}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
        val backgroundColor = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f) else Color(0xFFE8F5E9)
        val contentColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)

        Surface(
            modifier = modifier.fillMaxWidth(),
            color = backgroundColor,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isError) Icons.Default.Info else Icons.Default.AssignmentTurnedIn,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = error ?: success ?: "",
                    color = contentColor,
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado de tu Carga",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Mantenibilidad SonarQube: Un solo IF asigna de forma limpia el Badge
                val (badgeText, badgeBg, badgeValueColor) = if (carga != null) {
                    Triple("CON CARGA", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                } else {
                    Triple("VACÍO", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeValueColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SOLUCIÓN AL SUBRAYADO ROJO:
            // Usamos `let` para garantizar a Kotlin un Smart Cast seguro e inmutable de 'carga'
            if (carga != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Material", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text(carga.tipoMaterial, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Cantidad Disponible", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        // .toString() asegura que si cantidadDisponible es Int/Double, no rompa el Text
                        Text(
                            text = carga.cantidadDisponible.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Text(
                    text = "Aún no tienes carga registrada para iniciar tu jornada.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SubirCargaSection(
    tipoMaterialSeleccionado: String,
    cantidadTexto: String,
    onTipoMaterialChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onSubirCarga: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Nueva Carga / Reemplazar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Selecciona el tipo de material",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            MaterialSelectorGroup(
                selectedValue = tipoMaterialSeleccionado,
                onValueChange = onTipoMaterialChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cantidadTexto,
                onValueChange = onCantidadChange,
                label = { Text("Cantidad total disponible") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSubirCarga,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrar Nueva Carga", fontWeight = FontWeight.Bold)
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
    // Regla de Mantenibilidad: Evita recrear la lista en cada recomposición
    val opciones = remember { listOf("PANDERETA", "TECHO") }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        opciones.forEach { opcion ->
            val isSelected = selectedValue == opcion

            // SonarQube: Extraer los estilos calculados limpia los parámetros del Composable
            val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
            val borderWith = if (isSelected) 2.dp else 1.dp
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .border(borderWith, borderColor, RoundedCornerShape(12.dp))
                    .clickable { onValueChange(opcion) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opcion,
                    fontWeight = fontWeight,
                    color = textColor,
                    fontSize = 14.sp
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Aumentar Carga Actual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Añade stock solo si coincide con el material que ya llevas.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cantidadAumentarTexto,
                onValueChange = onCantidadAumentarChange,
                label = { Text("Cantidad a agregar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAumentarCarga,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Añadir a la Carga", fontWeight = FontWeight.Bold)
            }
        }
    }
}