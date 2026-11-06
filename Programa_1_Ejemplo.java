public class Programa_1_Ejemplo {
        private int[] numeros;
        private int suma;


        public Programa_1_Ejemplo(int[] numeros) {
            this.numeros = numeros;
            this.suma = 0;
        }


        public void calcularSumatoria() {
            for(int i = 0; i < numeros.length; i++) {
                this.suma += numeros[i] * numeros[i];
            }
        }

        public void mostrarResultado() {
            System.out.print("Arreglo: ");
            for(int i = 0; i < numeros.length; i++) {
                System.out.print(numeros[i] + " ");
            }
            System.out.println("\nSumatoria de los cuadrados: " + this.suma);
        }
        public static void main(String[] args) {
            int[] numeros = {1, 2, 3, 4, 5};


            Programa_1_Ejemplo Sumatoria = new Programa_1_Ejemplo(numeros);


            Sumatoria.calcularSumatoria();
            Sumatoria.mostrarResultado();
        }
    }
