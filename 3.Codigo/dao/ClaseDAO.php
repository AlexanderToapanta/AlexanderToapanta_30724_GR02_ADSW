<?php

require_once __DIR__ . '/../models/Clase.php';

class ClaseDAO
{
    private PDO $conexion;

    public function __construct(?PDO $conexion = null)
    {
        $this->conexion = $conexion ?? $this->crearConexionPdoSimulada();
        $this->inicializarEsquemaSimulado();
    }

    private function crearConexionPdoSimulada(): PDO
    {
        $directorioDatos = __DIR__ . '/../data';
        if (!is_dir($directorioDatos)) {
            mkdir($directorioDatos, 0777, true);
        }

        $rutaBase = $directorioDatos . '/ironclad_box.sqlite';
        $pdo = new PDO('sqlite:' . $rutaBase);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);

        return $pdo;

        /*
        Para MySQL real:
        $dsn = 'mysql:host=localhost;dbname=ironclad_box;charset=utf8mb4';
        return new PDO($dsn, 'usuario', 'password', [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        ]);
        */
    }

    private function inicializarEsquemaSimulado(): void
    {
        $this->conexion->exec(
            'CREATE TABLE IF NOT EXISTS entrenadores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                disponible INTEGER NOT NULL DEFAULT 1
            )'
        );

        $this->conexion->exec(
            'CREATE TABLE IF NOT EXISTS clases (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dia TEXT NOT NULL,
                hora TEXT NOT NULL,
                duracion INTEGER NOT NULL,
                cupo_maximo INTEGER NOT NULL,
                cupos_disponibles INTEGER NOT NULL,
                entrenador_id INTEGER NOT NULL,
                FOREIGN KEY (entrenador_id) REFERENCES entrenadores(id)
            )'
        );

        $entrenadores = [
            ['nombre' => 'Valeria Rios', 'email' => 'valeria.rios@ironcladbox.local'],
            ['nombre' => 'Mateo Silva', 'email' => 'mateo.silva@ironcladbox.local'],
            ['nombre' => 'Camila Torres', 'email' => 'camila.torres@ironcladbox.local'],
        ];

        $sentencia = $this->conexion->prepare(
            'INSERT OR IGNORE INTO entrenadores (nombre, email, disponible) VALUES (:nombre, :email, 1)'
        );

        foreach ($entrenadores as $entrenador) {
            $sentencia->execute($entrenador);
        }
    }

    public function crear(Clase $clase): Clase
    {
        $sentencia = $this->conexion->prepare(
            'INSERT INTO clases
                (dia, hora, duracion, cupo_maximo, cupos_disponibles, entrenador_id)
             VALUES
                (:dia, :hora, :duracion, :cupo_maximo, :cupos_disponibles, :entrenador_id)'
        );

        $sentencia->execute([
            'dia' => $clase->getDia(),
            'hora' => $clase->getHora(),
            'duracion' => $clase->getDuracion(),
            'cupo_maximo' => $clase->getCupoMaximo(),
            'cupos_disponibles' => $clase->getCuposDisponibles(),
            'entrenador_id' => $clase->getEntrenadorId(),
        ]);

        return $this->buscarPorId((int) $this->conexion->lastInsertId());
    }

    public function actualizar(Clase $clase): Clase
    {
        $sentencia = $this->conexion->prepare(
            'UPDATE clases
                SET dia = :dia,
                    hora = :hora,
                    duracion = :duracion,
                    cupo_maximo = :cupo_maximo,
                    cupos_disponibles = :cupos_disponibles,
                    entrenador_id = :entrenador_id
              WHERE id = :id'
        );

        $sentencia->execute([
            'id' => $clase->getId(),
            'dia' => $clase->getDia(),
            'hora' => $clase->getHora(),
            'duracion' => $clase->getDuracion(),
            'cupo_maximo' => $clase->getCupoMaximo(),
            'cupos_disponibles' => $clase->getCuposDisponibles(),
            'entrenador_id' => $clase->getEntrenadorId(),
        ]);

        return $this->buscarPorId((int) $clase->getId());
    }

    public function eliminar(int $id): bool
    {
        $sentencia = $this->conexion->prepare('DELETE FROM clases WHERE id = :id');
        $sentencia->execute(['id' => $id]);

        return $sentencia->rowCount() > 0;
    }

    public function buscarPorId(int $id): ?Clase
    {
        $sentencia = $this->conexion->prepare('SELECT * FROM clases WHERE id = :id');
        $sentencia->execute(['id' => $id]);
        $fila = $sentencia->fetch();

        return $fila ? Clase::fromArray($fila) : null;
    }

    public function listar(): array
    {
        $sentencia = $this->conexion->query(
            'SELECT
                c.*,
                e.nombre AS entrenador_nombre,
                e.email AS entrenador_email
             FROM clases c
             INNER JOIN entrenadores e ON e.id = c.entrenador_id
             ORDER BY c.dia ASC, c.hora ASC'
        );

        return array_map([$this, 'mapearClaseConEntrenador'], $sentencia->fetchAll());
    }

    public function listarEntrenadores(): array
    {
        $sentencia = $this->conexion->query(
            'SELECT id, nombre, email, disponible FROM entrenadores ORDER BY nombre ASC'
        );

        return $sentencia->fetchAll();
    }

    public function entrenadorExisteYDisponible(int $entrenadorId): bool
    {
        $sentencia = $this->conexion->prepare(
            'SELECT COUNT(*) FROM entrenadores WHERE id = :id AND disponible = 1'
        );
        $sentencia->execute(['id' => $entrenadorId]);

        return (int) $sentencia->fetchColumn() > 0;
    }

    public function existeSolapamientoGeneral(
        string $dia,
        string $hora,
        int $duracion,
        ?int $idIgnorado = null
    ): bool {
        return $this->existeSolapamiento($dia, $hora, $duracion, null, $idIgnorado);
    }

    public function existeSolapamientoEntrenador(
        string $dia,
        string $hora,
        int $duracion,
        int $entrenadorId,
        ?int $idIgnorado = null
    ): bool {
        return $this->existeSolapamiento($dia, $hora, $duracion, $entrenadorId, $idIgnorado);
    }

    private function existeSolapamiento(
        string $dia,
        string $hora,
        int $duracion,
        ?int $entrenadorId,
        ?int $idIgnorado
    ): bool {
        $parametros = [
            'dia' => $dia,
            'inicio' => $hora,
            'fin' => $this->calcularHoraFin($hora, $duracion),
        ];

        $sql = 'SELECT COUNT(*)
                  FROM clases
                 WHERE dia = :dia
                   AND time(hora) < time(:fin)
                   AND time(:inicio) < time(hora, "+" || duracion || " minutes")';

        if ($entrenadorId !== null) {
            $sql .= ' AND entrenador_id = :entrenador_id';
            $parametros['entrenador_id'] = $entrenadorId;
        }

        if ($idIgnorado !== null) {
            $sql .= ' AND id <> :id_ignorado';
            $parametros['id_ignorado'] = $idIgnorado;
        }

        $sentencia = $this->conexion->prepare($sql);
        $sentencia->execute($parametros);

        return (int) $sentencia->fetchColumn() > 0;
    }

    private function calcularHoraFin(string $hora, int $duracion): string
    {
        $inicio = DateTime::createFromFormat('H:i', $hora);
        $inicio->modify('+' . $duracion . ' minutes');

        return $inicio->format('H:i');
    }

    private function mapearClaseConEntrenador(array $fila): array
    {
        $clase = Clase::fromArray($fila)->toArray();
        $clase['entrenador'] = [
            'id' => (int) $fila['entrenador_id'],
            'nombre' => $fila['entrenador_nombre'],
            'email' => $fila['entrenador_email'],
        ];

        return $clase;
    }
}
