<?php

require_once __DIR__ . '/../dao/ReservaDAO.php';

class ReservaService
{
    private ReservaDAO $reservaDAO;

    public function __construct(?ReservaDAO $reservaDAO = null)
    {
        $this->reservaDAO = $reservaDAO ?? new ReservaDAO();
    }

    public function listarAtletas(): array
    {
        return $this->reservaDAO->listarAtletas();
    }

    public function listarClasesDisponibles(int $idAtleta): array
    {
        $this->validarAtleta($idAtleta);
        return $this->reservaDAO->listarClasesDisponibles($idAtleta);
    }

    public function listarReservasActivas(int $idAtleta): array
    {
        $this->validarAtleta($idAtleta);
        return $this->reservaDAO->listarReservasActivas($idAtleta);
    }

    public function reservar(array $datos): Reserva
    {
        $idAtleta = (int) ($datos['idAtleta'] ?? $datos['id_atleta'] ?? 0);
        $idClase = (int) ($datos['idClase'] ?? $datos['id_clase'] ?? 0);

        $this->validarAtleta($idAtleta);

        if ($idClase <= 0 || !$this->reservaDAO->claseExiste($idClase)) {
            throw new InvalidArgumentException('Debe seleccionar una clase valida.');
        }

        if (!$this->validarMembresiaVigente($idAtleta)) {
            throw new DomainException('El atleta no tiene una membresia pagada y vigente.');
        }

        $cupos = $this->reservaDAO->consultarCupos($idClase);
        if ($cupos === null || $cupos <= 0) {
            throw new DomainException('La clase seleccionada no tiene cupos disponibles.');
        }

        if ($this->reservaDAO->existeReservaActiva($idAtleta, $idClase)) {
            throw new DomainException('El atleta ya tiene una reserva activa para esta clase.');
        }

        return $this->reservaDAO->crearReservaYAjustarCupo($idAtleta, $idClase);
    }

    public function cancelar(array $datos): void
    {
        $idAtleta = (int) ($datos['idAtleta'] ?? $datos['id_atleta'] ?? 0);
        $idReserva = (int) ($datos['id'] ?? $datos['idReserva'] ?? $datos['id_reserva'] ?? 0);

        $this->validarAtleta($idAtleta);

        if ($idReserva <= 0) {
            throw new InvalidArgumentException('Debe indicar una reserva valida.');
        }

        $this->reservaDAO->cancelarReservaYLiberarCupo($idReserva, $idAtleta);
    }

    private function validarMembresiaVigente(int $idAtleta): bool
    {
        return $this->reservaDAO->validarMembresiaVigente($idAtleta, date('Y-m-d'));
    }

    private function validarAtleta(int $idAtleta): void
    {
        if ($idAtleta <= 0 || !$this->reservaDAO->atletaExiste($idAtleta)) {
            throw new InvalidArgumentException('Debe seleccionar un atleta valido.');
        }
    }
}
