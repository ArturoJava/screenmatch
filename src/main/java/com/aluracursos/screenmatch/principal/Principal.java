package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import static java.util.stream.Nodes.collect;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=ad0ff7cd";
    private ConvierteDatos conversor =new ConvierteDatos();

    public void muestraElMenu(){
       System.out.println("Por favor escribe el nombre de la Serie que deseas buscar");
       //Busca los datos genrales de las series
       var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE+nombreSerie.replace(" ","+") + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        ///busca los datos de todas las tempordas
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i <= datos.totalTemporadas(); i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+")+"&Season="+i+API_KEY);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporadas);
        }
       //temporadas.forEach(System.out::println);

        //Mostrar solo el titulo de los episodios para las temporadas
//        for (int i = 0; i < datos.totalTemporadas(); i++) {
//            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).espisodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//
//            }
//
//        }
       // temporadas.forEach(t ->t.episodios().forEach(e -> System.out.println(e.titulo())));
        //Convertir todas las informaciones a una lista del tipo DatosEpisodio

        List<DatosEpisodio> datosEpisodios =  temporadas.stream()
                .flatMap(t-> t.episodios().stream())
                .collect(Collectors.toList());
        //Top 5 episodios
//        System.out.println("Top 5 episodios");
//        datosEpisodios.stream()
//                .filter(e->!e.evaluacion().equalsIgnoreCase("N/A"))
//                .peek(e-> System.out.println("Primer filtro (N/A)"+e))
//                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//                .peek(e-> System.out.println("Segundo filtro (M/m)"+e))
//                .map(e->e.titulo().toUpperCase())
//                .peek(e-> System.out.println("Tercer filtro (m/M)"+e))
//                .limit(5)
//                .forEach(System.out::println);

        //Convirtiendo lo datos a una lista del tipo Episodio
        List<Episodio> episodios = temporadas.stream()
                    .flatMap(t->t.episodios().stream()
                         .map(d-> new Episodio(t.numero(),d)))
                .collect(Collectors.toList());

        //episodios.forEach(System.out::println);

        //Busqueda de episodios a partir de x año
//        System.out.println("Por favor indica el año a partir del cual deseas ver los episodios");
//        var fecha = teclado.nextInt();
//        teclado.nextLine();

        //LocalDate fechaBusqueda = LocalDate.of(fecha,1,1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e->e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
//                .forEach(e-> System.out.println(
//                        "Temporada "+e.getTemporada()+
//                                "Episodio "+e.getTitulo()+
//                                "Fecha de Lanzamiento " +e.getFechaDeLanzamiento().format(dtf)
//                ));

        //Busca episodios porun pedazo del titulo
//        System.out.println("por favor escriba el titulo del espisodio que desea ver");
//        var pedazotitulo = teclado.nextLine();
//
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(pedazotitulo.toUpperCase()))
//                .findFirst();
//        if(episodioBuscado.isPresent()){
//            System.out.println("Episodio Encontrado");
//            System.out.println("Los datos son:" + episodioBuscado.get());
//        }else{
//            System.out.println("Episodio no econtrado");
//        }
        Map<Integer, Double> evaluacionsPorTemporada = episodios.stream()
                .filter(e->e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println(evaluacionsPorTemporada);
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e->e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("Media de las evaluciones: "+est.getAverage());
        System.out.println("Episodio mejor evaluado: "+est.getMax());
        System.out.println("Episodio peor evaluado: "+est.getMin());
        System.out.println("Cantidad " + est.getCount());


    }
}












