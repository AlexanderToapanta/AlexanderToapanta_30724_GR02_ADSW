<?php

require_once __DIR__ . '/../builders/ClaseBuilder.php';
require_once __DIR__ . '/../dao/ClaseDAO.php';

class ClaseService
{
    private const CAPACIDAD_CROSSFIT = 30;

    private ClaseDAO $claseDAO;

    public function __construct(?ClaseDAO $claseDAO = null)
    {
        $this->claseDAO = $claseDAO ?? new ClaseDAO();
    }

    public function listar(): array
    {
        return $this->claseDAO->listar();
    }

    public function listarEntrenadores(): array
    {
        return $this->claseDAO->listarEntrenadores();
    }

    public function crear(array $datos): Clase
    {
        $datos = $this->normalizarDatos($datos);
        $clase = $this->crearBuilderDesdeDatos($datos)->construir();

        $this->validarReglasDeAgenda($clase);

        return $this->claseDAO->crear($clase);
    }

    public function editar(int $id, array $datos): Clase
    {
        $claseActual = $this->claseDAO->buscarPorId($id);
        if (!$claseActual) {
            throw new DomainException('La clase solicitada no existe.');
        }

        $datos = $this->normalizarDatos($datos);
        $cuposDisponibles = isset($datos['cuposDisponibles'])
            ? (int) $datos['cuposDisponibles']
            : min($claseActual->getCuposDisponibles(), (int) $datos['cupoMaximo']);

        $clase = $this->crearBuilderDesdeDatos($datos)
            ->conId($id)
            ->definirCuposDisponibles($cuposDisponibles)
            ->construir();

        $this->validarReglasDeAgenda($clase, $id);

        return $this->claseDAO->actualizar($clase);
    }

    public function eliminar(int $id): void
    {
        if (!$this->claseDAO->eliminar($id)) {
            throw new DomainException('La clase solicitada no existe o ya fue eliminada.');
        }
    }

    private function crearBuilderDesdeDatos(array $datos): ClaseBuilder
    {
        return (new ClaseBuilder())
            ->definirDiaHora($datos['dia'], $datos['hora'])
            ->definirDuracion((int) $datos['duracion'])
            ->definirCupoMaximo((int) $datos['cupoMaximo'], self::CAPACIDAD_CROSSFIT)
            ->asignarEntrenador((int) $datos['entrenadorId']);
    }

    private function validarReglasDeAgenda(Clase $clase, ?int $idIgnorado = null): void
    {
        if (!$this->claseDAO->entrenadorExisteYDisponible($clase->getEntrenadorId())) {
            throw new DomainException('El entrenador asignado no existe o no esta disponible.');
        }

        if ($this->claseDAO->existeSolapamientoEntrenador(
            $clase->getDia(),
            $clase->getHora(),
            $clase->getDuracion(),
            $clase->getEntrenadorId(),
            $idIgnorado
        )) {
            throw new DomainException('El entrenador asignado no esta disponible en ese horario.');
        }

        if ($this->claseDAO->existeSolapamientoGeneral(
            $clase->getDia(),
            $clase->getHora(),
            $clase->getDuracion(),
            $idIgnorado
        )) {
            throw new DomainException('El horario se solapa con otra clase existente.');
        }
    }

    private function normalizarDatos(array $datos): array
    {
        $normalizados = [
            'dia' => $datos['dia'] ?? '',
            'hora' => $datos['hora'] ?? '',
            'duracion' => $datos['duracion'] ?? null,
            'cupoMaximo' => $datos['cupoMaximo'] ?? $datos['cupo_maximo'] ?? null,
            'cuposDisponibles' => $datos['cuposDisponibles'] ?? $datos['cupos_disponibles'] ?? null,
            'entrenadorId' => $datos['entrenadorId'] ?? $datos['entrenador_id'] ?? null,
        ];

        foreach (['dia', 'hora', 'duracion', 'cupoMaximo', 'entrenadorId'] as $campo) {
            if ($normalizados[$campo] === null || $normalizados[$campo] === '') {
                throw new InvalidArgumentException('El campo ' . $campo . ' es obligatorio.');
            }
        }

        return $normalizados;
    }
}
