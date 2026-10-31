package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        mostrarMenu();
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

    // TEMPORAL (se reemplaza en el paso 4)
    static int celdaDisponible() {
        return -1;
    }

    // TEMPORAL (se reemplaza en el paso 5)
    static String leerTipoVehiculo() {
        return CARRO;
    }

    // TEMPORAL (se reemplaza en el paso 5)
    static LocalDateTime leerFechaHora(String mensaje) {
        return LocalDateTime.now();
    }

}
