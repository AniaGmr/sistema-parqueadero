package application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Main {

    static final int CAPACIDAD_TOTAL = 10;
    static final int TARIFA_HORA_CARRO = 4000;
    static final int TARIFA_DIA_CARRO = 30000;
    static final int TARIFA_HORA_MOTO = 2000;
    static final int TARIFA_DIA_MOTO = 15000;
    static final int UMBRAL_HORAS_DIA = 8;
    static final int HORA_APERTURA = 8;
    static final int HORA_CIERRE = 22;

    static final String CARRO = "CARRO";
    static final String MOTO = "MOTO";

    static final Scanner SCANNER = new Scanner(System.in);
    static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static String[] tipos = new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] ingresos = new LocalDateTime[CAPACIDAD_TOTAL];

    static int celdasOcupadas;
    static int celdasLibres;
    static long recaudoCarros;
    static long recaudoMotos;

    public static void main(String[] args) {
        inicializarArreglos();
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEnteroEnRango("Seleccione una opción: ", 1, 5);
            ejecutarOpcion(opcion);
        } while (opcion != 5);
        SCANNER.close();
    }

    static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> registrarEntrada();
            case 2 -> registrarSalida();
            case 3 -> reporteOcupacion();
            case 4 -> reporteFinanciero();
            case 5 -> System.out.println("Programa finalizado.");
        }
    }

    static void mostrarMenu() {

        System.out.println("===== ECOPARKING =====");
        System.out.println("1. Registrar entrada.");
        System.out.println("2. Registrar salida.");
        System.out.println("3. Revisar ocupación.");
        System.out.println("4. Reporte financiero.");
        System.out.println("5. Salir");
    }

    static void inicializarArreglos() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            tipos[i] = null;
            ingresos[i] = null;
        }
        celdasOcupadas = 0;
        celdasLibres = CAPACIDAD_TOTAL;
        recaudoCarros = 0;
        recaudoMotos = 0;
    }

    static void registrarEntrada() {
        int indice = celdaDisponible();
        if (indice == -1) {
            System.out.println("El parqueadero está lleno. No se puede registrar el ingreso.");
            return;
        }
        String tipo = leerTipoVehiculo();
        LocalDateTime ingreso = leerFechaHora("Fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");

        tipos[indice] = tipo;
        ingresos[indice] = ingreso;
        celdasOcupadas++;
        celdasLibres--;
        System.out.println("Vehículo (" + tipo + ") asignado a la celda " + (indice + 1) + ".");
    }

    static int celdaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tipos[i] == null) {
                return i;
            }
        }
        return -1;
    }

    static int leerEnteroEnRango(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            if (!texto.isEmpty() && texto.length() <= 9) {
                int valor = leerDigitos(texto, 0, texto.length());
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            }
            System.out.println("Dato inválido. Ingrese un número entre " + minimo + " y " + maximo + ".");
        }
    }

    static String leerTipoVehiculo() {
        int opcion = leerEnteroEnRango("Tipo de vehículo (1. Carro, 2. Moto): ", 1, 2);
        return opcion == 1 ? CARRO : MOTO;
    }

    static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();

            if (!formatoFechaHoraValido(texto)) {
                System.out.println("Fecha y hora inválidas. Use yyyy-MM-dd HH:mm y el horario 08:00-22:00.");
                continue;
            }

            LocalDateTime fecha = LocalDateTime.parse(texto, FORMATO_FECHA_HORA);

            if (!fecha.format(FORMATO_FECHA_HORA).equals(texto)) {
                System.out.println("La fecha no existe en el calendario.");
                continue;
            }

            return fecha;
        }
    }

    static boolean formatoFechaHoraValido(String texto) {
        if (texto.length() != 16
                || texto.charAt(4) != '-'
                || texto.charAt(7) != '-'
                || texto.charAt(10) != ' '
                || texto.charAt(13) != ':') {
            return false;
        }

        int anio = leerDigitos(texto, 0, 4);
        int mes = leerDigitos(texto, 5, 2);
        int dia = leerDigitos(texto, 8, 2);
        int hora = leerDigitos(texto, 11, 2);
        int minuto = leerDigitos(texto, 14, 2);

        return anio >= 1 && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31
                && hora >= HORA_APERTURA && hora <= HORA_CIERRE
                && (hora != HORA_CIERRE || minuto <= 0)
                && minuto >= 0 && minuto <= 59;
    }

    static int leerDigitos(String texto, int inicio, int cantidad) {
        int valor = 0;
        for (int i = inicio; i < inicio + cantidad; i++) {
            char caracter = texto.charAt(i);
            if (caracter < '0' || caracter > '9') {
                return -1;
            }
            valor = valor * 10 + caracter - '0';
        }
        return valor;
    }

    static void registrarSalida() {
        int celda = leerEnteroEnRango("Número de celda (1-" + CAPACIDAD_TOTAL + "): ", 1, CAPACIDAD_TOTAL);
        int indice = celda - 1;
        if (tipos[indice] == null) {
            System.out.println("La celda " + celda + " está libre; no hay vehículo para liquidar.");
            return;
        }

        LocalDateTime salida;
        while (true) {
            salida = leerFechaHora("Fecha y hora de salida (yyyy-MM-dd HH:mm): ");
            if (!salida.isBefore(ingresos[indice])) {
                break;
            }
            System.out.println("La salida no puede ser anterior al ingreso ("
                    + ingresos[indice].format(FORMATO_FECHA_HORA) + ").");
        }

        long total = liquidar(indice, salida);
        if (tipos[indice].equals(CARRO)) {
            recaudoCarros += total;
        } else {
            recaudoMotos += total;
        }
        tipos[indice] = null;
        ingresos[indice] = null;
        celdasOcupadas--;
        celdasLibres++;
    }

    static long liquidar(int indice, LocalDateTime salida) {
        LocalDateTime ingreso = ingresos[indice];
        boolean esCarro = tipos[indice].equals(CARRO);
        long tarifaDia = esCarro ? TARIFA_DIA_CARRO : TARIFA_DIA_MOTO;
        long tarifaHora = esCarro ? TARIFA_HORA_CARRO : TARIFA_HORA_MOTO;

        long diasACobrar = contarDiasCompletos(ingreso, salida);
        LocalDateTime cursor = ingreso.plusDays(diasACobrar);
        long horasACobrar = calcularHorasRestantes(cursor, salida);

        if (horasACobrar >= UMBRAL_HORAS_DIA) {
            diasACobrar++;
            horasACobrar = 0;
        }

        long total = diasACobrar * tarifaDia + horasACobrar * tarifaHora;
        mostrarFactura(indice, salida, diasACobrar, tarifaDia, horasACobrar, tarifaHora, total);
        return total;
    }

    static long contarDiasCompletos(LocalDateTime ingreso, LocalDateTime salida) {
        return ChronoUnit.DAYS.between(ingreso, salida);
    }

    static long calcularHorasRestantes(LocalDateTime cursor, LocalDateTime salida) {
        long minutosRestantes = Duration.between(cursor, salida).toMinutes();
        return (minutosRestantes + 59) / 60;
    }

    static void mostrarFactura(int indice, LocalDateTime salida, long dias, long tarifaDia,
                               long horas, long tarifaHora, long total) {
        System.out.println("========== FACTURA ==========");
        System.out.println("Celda liberada: " + (indice + 1));
        System.out.println("Tipo de vehículo: " + tipos[indice]);
        System.out.println("Ingreso: " + ingresos[indice].format(FORMATO_FECHA_HORA));
        System.out.println("Salida:  " + salida.format(FORMATO_FECHA_HORA));
        if (dias > 0) {
            System.out.println(dias + " día(s) x " + formatoMoneda(tarifaDia) + " = " + formatoMoneda(dias * tarifaDia));
        }
        if (horas > 0) {
            System.out.println(horas + " hora(s) x " + formatoMoneda(tarifaHora) + " = " + formatoMoneda(horas * tarifaHora));
        }
        System.out.println("TOTAL A PAGAR: " + formatoMoneda(total));
        System.out.println("=============================");
    }

    static String formatoMoneda(long valor) {
        return String.format("$%,d", valor);
    }

    static void reporteOcupacion() {
        System.out.println("--- Ocupación celdas ---");
        System.out.println("Celdas ocupadas: " + celdasOcupadas);
        System.out.println("Celdas libres: " + celdasLibres);
    }

    static void reporteFinanciero() {
        System.out.println("--- Reporte financiero ---");
        System.out.println("Recaudo carros: " + formatoMoneda(recaudoCarros));
        System.out.println("Recaudo motos: " + formatoMoneda(recaudoMotos));
        System.out.println("Total general: " + formatoMoneda(recaudoCarros + recaudoMotos));
    }

}
