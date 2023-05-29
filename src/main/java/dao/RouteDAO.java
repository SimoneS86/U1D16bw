package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import entities.Route;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteDAO {
	private final EntityManager em;

	public RouteDAO(EntityManager em) {
		this.em = em;
	}

	public void saveRoute(Route route) {
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			em.persist(route);
			transaction.commit();
			log.info("Route salvato correttamente: " + route);
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error("Errore durante il salvataggio della Route.", e);
			throw e;
		}
	}

	public List<Route> getAllRoutes() {
		try {
			TypedQuery<Route> query = em.createQuery("SELECT u FROM Route u", Route.class);
			List<Route> routeList = query.getResultList();
			StringBuilder routes = new StringBuilder("Lista Tratte:\n");
			for (Route route : routeList) {
				routes.append(route.toString()).append("\n");
			}
			System.out.println(routes.toString());
			return routeList;
		} catch (Exception e) {
			log.error("error: " + e);
			throw e;
		}

	};

	public Route getRouteById(Long id) {
		return em.find(Route.class, id);
	}

	public void deleteRoute(Long id) {
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			Route route = em.find(Route.class, id);
			if (route != null) {
				em.remove(route);
				transaction.commit();
				log.info("Viaggio eliminato correttamente: " + route);
			} else {
				log.warn("Impossibile trovare il viaggio con ID: " + id);
			}
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error("Errore durante l'eliminazione del viaggio.", e);
			throw e;
		}
	}
	
	public List<Map.Entry<UUID, UUID>> findVehicleIdsWithShortestTravelTime() {
		List<Map.Entry<UUID, UUID>> vehicleIds = new ArrayList<>();
		Map<String, List<Route>> routeMap = new HashMap<>();

		try {
			TypedQuery<Route> query = em.createQuery("SELECT r FROM Route r", Route.class);
			List<Route> routes = query.getResultList();

			// Raggruppa le route in base a startPoint e endPoint
			for (Route route : routes) {
				String key = route.getStartPoint() + "-" + route.getTerminus();
				routeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(route);
			}

			// Trova il percorso con il tempo di viaggio più breve per ogni coppia
			// startPoint-endPoint
			for (List<Route> routeGroup : routeMap.values()) {
				if (routeGroup.size() == 1) {
					// Solo una route per questa coppia, aggiungi direttamente il veicolo
					// corrispondente
					vehicleIds.add(Map.entry(routeGroup.get(0).getId(), routeGroup.get(0).getVehicle().getId()));
				} else {
					// Più di una route per questa coppia, cerca quella con il tempo di viaggio più
					// breve
					Route shortestTravelTimeRoute = null;
					for (Route route : routeGroup) {
						if (shortestTravelTimeRoute == null
								|| route.getTravelTime().compareTo(shortestTravelTimeRoute.getTravelTime()) < 0) {
							shortestTravelTimeRoute = route;
						}
					}
					vehicleIds.add(
							Map.entry(shortestTravelTimeRoute.getId(), shortestTravelTimeRoute.getVehicle().getId()));
				}
			}
			for (Map.Entry<UUID, UUID> entry : vehicleIds) {
				UUID routeId = entry.getKey();
				UUID vehicleId = entry.getValue();

				// Recupera la route corrispondente all'ID
				Route route = em.find(Route.class, routeId);

				// Stampa i dettagli utilizzando il logger
				log.info("[" + route.getStartPoint() + " - " + route.getTerminus() + "]" + " BestVehicleId: "
						+ vehicleId);
			}
		} catch (Exception e) {
			log.error("Errore durante la ricerca dei veicoli con tempo di viaggio più breve.", e);
			throw e;
		}

		return vehicleIds;
	}

}