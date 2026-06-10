<?php

require_once __DIR__ . '/../services/ReservaService.php';

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

$service = new ReservaService();
$payload = obtenerPayloadReserva();
$accion = $_GET['action'] ?? $payload['action'] ?? 'clases';

try {
    switch ($accion) {
        case 'atletas':
            responderReserva(['success' => true, 'data' => $service->listarAtletas()]);
            break;

        case 'clases':
            responderReserva([
                'success' => true,
                'data' => $service->listarClasesDisponibles(obtenerIdAtletaReserva($payload)),
            ]);
            break;

        case 'misReservas':
            responderReserva([
                'success' => true,
                'data' => $service->listarReservasActivas(obtenerIdAtletaReserva($payload)),
            ]);
            break;

        case 'reservar':
            asegurarPostReserva();
            $reserva = $service->reservar($payload);
            responderReserva([
                'success' => true,
                'message' => 'Reserva confirmada correctamente.',
                'data' => $reserva->toArray(),
            ], 201);
            break;

        case 'cancelar':
            asegurarPostReserva();
            $service->cancelar($payload);
            responderReserva(['success' => true, 'message' => 'Reserva cancelada correctamente.']);
            break;

        default:
            responderReserva(['success' => false, 'message' => 'Accion no soportada.'], 404);
    }
} catch (InvalidArgumentException | DomainException $error) {
    responderReserva(['success' => false, 'message' => $error->getMessage()], 422);
} catch (Throwable $error) {
    responderReserva(['success' => false, 'message' => 'Error interno del servidor.'], 500);
}

function obtenerPayloadReserva(): array
{
    $contenido = file_get_contents('php://input');
    $json = json_decode($contenido ?: '{}', true);

    if (json_last_error() === JSON_ERROR_NONE && is_array($json) && $json !== []) {
        return $json;
    }

    return $_POST;
}

function obtenerIdAtletaReserva(array $payload): int
{
    return (int) ($_GET['idAtleta'] ?? $payload['idAtleta'] ?? $payload['id_atleta'] ?? 0);
}

function asegurarPostReserva(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new InvalidArgumentException('La operacion requiere metodo POST.');
    }
}

function responderReserva(array $respuesta, int $estadoHttp = 200): void
{
    http_response_code($estadoHttp);
    echo json_encode($respuesta, JSON_UNESCAPED_UNICODE);
    exit;
}
