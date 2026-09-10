// ==================== Main.java ====================

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Suma suma = new Suma();
        Resta resta = new Resta();
        Multiplicacion multiplicacion = new Multiplicacion();
        Division division = new Division();
        Potencia potencia = new Potencia();
        Raiz raiz = new Raiz();
        Modulo modulo = new Modulo();
        Porcentaje porcentaje = new Porcentaje();
        Promedio promedio = new Promedio();
        Maximo maximo = new Maximo();
        Minimo minimo = new Minimo();
        Absoluto absoluto = new Absoluto();

        int opcion;
        double a, b;

        do {
            System.out.println("\n--- CALCULADORA ---");
            System.out.println("1. Suma");
            System.out.println("2. Resta");
            System.out.println("3. Multiplicacion");
            System.out.println("4. Division");
            System.out.println("5. Potencia");
            System.out.println("6. Raiz");
            System.out.println("7. Modulo");
            System.out.println("8. Porcentaje");
            System.out.println("9. Promedio");
            System.out.println("10. Maximo");
            System.out.println("11. Minimo");
            System.out.println("12. Absoluto");
            System.out.println("0. Salir");

            System.out.print("Opcion: ");
            opcion = sc.nextInt();

            if (opcion >= 1 && opcion <= 12) {

                System.out.print("Numero: ");
                a = sc.nextDouble();

                if (opcion != 6 && opcion != 12) {
                    System.out.print("Segundo numero: ");
                    b = sc.nextDouble();
                } else {
                    b = 0;
                }

                switch (opcion) {

                    case 1:
                        System.out.println("Resultado: " + suma.calcular(a, b));
                        break;

                    case 2:
                        System.out.println("Resultado: " + resta.calcular(a, b));
                        break;

                    case 3:
                        System.out.println("Resultado: " + multiplicacion.calcular(a, b));
                        break;

                    case 4:
                        System.out.println("Resultado: " + division.calcular(a, b));
                        break;

                    case 5:
                        System.out.println("Resultado: " + potencia.calcular(a, b));
                        break;

                    case 6:
                        System.out.println("Resultado: " + raiz.calcular(a));
                        break;

                    case 7:
                        System.out.println("Resultado: " + modulo.calcular(a, b));
                        break;

                    case 8:
                        System.out.println("Resultado: " + porcentaje.calcular(a, b));
                        break;

                    case 9:
                        System.out.println("Resultado: " + promedio.calcular(a, b));
                        break;

                    case 10:
                        System.out.println("Resultado: " + maximo.calcular(a, b));
                        break;

                    case 11:
                        System.out.println("Resultado: " + minimo.calcular(a, b));
                        break;

                    case 12:
                        System.out.println("Resultado: " + absoluto.calcular(a));
                        break;
                }
            }

        } while (opcion != 0);

        System.out.println("Calculadora finalizada.");

    }
}
