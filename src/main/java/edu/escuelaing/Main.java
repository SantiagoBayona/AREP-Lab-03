package edu.escuelaing;

import java.util.HashMap;
import java.util.Map;

public class Main {

    private static Map<String, ServicioConParamStr> servicios = new HashMap<>();

    public static void main(String[] args) {
        get("/hello", str -> "Hola" + str);
        get("/author", str -> "Santiago");
        get("/cos", str -> {
           return "" + Math.cos(Double.parseDouble(str));
        });
        get("/sin", new ServicioConParamStr() {
            @Override
            public String execute(String str) {
                String v = str.split("=")[1];
                return "" + Math.sin(Double.parseDouble(v));
            }
        });

        ServicioConParamStr s = buscar("/hello");
        System.out.println("La función \"/hello\" retorna: " + s.execute("Santiago"));

        System.out.println(buscar("/author").execute("sff"));
        System.out.println(buscar("/sin").execute("1.6"));
        System.out.println(buscar("/cos").execute("2.3  "));

    }

    public static void get(String param, ServicioConParamStr servicio){
        servicios.put(param, servicio);
    }

    public static ServicioConParamStr buscar(String nombre){
        return servicios.get(nombre);
    }
}