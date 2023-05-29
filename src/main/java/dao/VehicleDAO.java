package dao;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import entities.Card;
import entities.Public_Transport_Pass;
import entities.Ticket;
import entities.Travel_Document;
import entities.Vehicle;
import entities.Vehicle.State;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VehicleDAO {
	private final EntityManager em;

	public VehicleDAO(EntityManager em) {
		this.em = em;
	}

	public void saveVehicle(Vehicle vehicle) {
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			em.persist(vehicle);
			transaction.commit();
			log.info("Veicolo salvato correttamente: " + vehicle);
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error("Errore durante il salvataggio del veicolo.", e);
			throw e;
		}
	}

	public void checkValidity(String idPass, String idCard) {
		Public_Transport_Pass pass = em.find(Public_Transport_Pass.class, UUID.fromString(idPass));
		Card card = em.find(Card.class, UUID.fromString(idCard));

		if (pass.getSubType() == Public_Transport_Pass.SubType.SETTIMANALE && card.isValidità() == true) {

			LocalDate dataEmissione = pass.getDataEmissione();
			LocalDate now = LocalDate.now();
			LocalDate expirationDate = dataEmissione.plusWeeks(1);

			if (now.isBefore(expirationDate)) {
				System.out.println("L'abbonamento settimanale è ancora valido.");
			} else {
				pass.setValid(false);
				System.out.println("L'abbonamento settimanale non è più valido.");
			}
		} else if (pass.getSubType() == Public_Transport_Pass.SubType.MENSILE && card.isValidità() == true) {

			LocalDate dataEmissione = pass.getDataEmissione();
			LocalDate now = LocalDate.now();
			LocalDate expirationDate = dataEmissione.plusMonths(1);

			if (now.isBefore(expirationDate)) {
				System.out.println("L'abbonamento mensile è ancora valido.");
			} else {
				pass.setValid(false);
				System.out.println("L'abbonamento mensile non è più valido.");
			}
		} else {
			System.out.println("Card dell'utente scaduta.");
		}
	}

	public void validateTicket(String travelDocID, String vID) {
		EntityTransaction transaction = null;

		try {
			transaction = em.getTransaction();
			transaction.begin();

			Travel_DocumentDAO tDAO = new Travel_DocumentDAO(em);
			Travel_Document td = tDAO.findByUUID(travelDocID);

			if (td instanceof Public_Transport_Pass) {
				if (((Public_Transport_Pass) td).isValid()) {
					log.info("Pass valido!");
				} else {
					log.info("Aaaaaaaah facciamo una bella multina qui!");
				}
			} else if (td instanceof Ticket) {
				Ticket ticket = (Ticket) td;

				if (!ticket.isEndorsed()) {
					Vehicle v = this.getVehicleById(UUID.fromString(vID));

					ticket.setEndorsed(true);
					ticket.setDataVid(LocalDate.now());
					log.info("Biglietto vidimato!");

					v.setTicketsValidated(v.getTicketsValidated() + 1);
				} else {
					log.info("Aaaaaaaah facciamo una bella multina qui!");
				}
			} else {
				log.info("Niente biglietto o abbonamento? Aaaaaaaah facciamo una bella multina qui!");
			}

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			log.error("An error occurred during ticket validation: " + e.getMessage());
		}
	}

	public Vehicle getVehicleById(UUID id) {
		Vehicle found = em.find(Vehicle.class, id);
		return found;
	}
	
	public Vehicle findVehicleWithHighestTicketsValidated() {
        try {
            TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v ORDER BY v.ticketsValidated DESC",
                    Vehicle.class);
            query.setMaxResults(1);

            List<Vehicle> vehicles = query.getResultList();
            if (!vehicles.isEmpty()) {
                Vehicle vehicleWithHighestTickets = vehicles.get(0);
                log.info("Veicolo che ha vidimato più biglietti: " + vehicleWithHighestTickets.toString());
                return vehicleWithHighestTickets;
            }

            return null; // Nessun veicolo presente
        } catch (Exception e) {
            log.error("Errore durante la ricerca del veicolo con il valore più alto per ticketsValidated.", e);
            throw e;
        }
    }

	public List<Vehicle> getAllVehicles() {
		try {
			TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v", Vehicle.class);
			List<Vehicle> vehicles = query.getResultList();
			for (Vehicle vehicle : vehicles) {
				log.info(vehicle.toString());
			}
			return vehicles;
		} catch (Exception e) {
			log.error("Errore durante il recupero dei veicoli.", e);
			throw e;
		}
	}
	
	public List<Vehicle> getAllVehiclesInService() {
	    try {
	        TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v WHERE v.state = :state", Vehicle.class);
	        query.setParameter("state", Vehicle.State.IN_SERVICE);
	        List<Vehicle> vehicles = query.getResultList();
	        StringBuilder sb = new StringBuilder("Veicoli in servizio:\n");
	        for (Vehicle vehicle : vehicles) {
	            sb.append(vehicle.toString()).append("\n");
	        }
	        log.info(sb.toString());
	        return vehicles;
	    } catch (Exception e) {
	        log.error("Errore durante il recupero dei veicoli in servizio.", e);
	        throw e;
	    }
	}

	public List<Vehicle> getAllVehiclesUnderMaintenance() {
	    try {
	        TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v WHERE v.state = :state", Vehicle.class);
	        query.setParameter("state", Vehicle.State.UNDER_MAINTENANCE);
	        List<Vehicle> vehicles = query.getResultList();
	        StringBuilder sb = new StringBuilder("Veicoli in manutenzione:\n");
	        for (Vehicle vehicle : vehicles) {
	            sb.append(vehicle.toString()).append("\n");
	        }
	        log.info(sb.toString());
	        return vehicles;
	    } catch (Exception e) {
	        log.error("Errore durante il recupero dei veicoli in manutenzione.", e);
	        throw e;
	    }
	}


	
	public List<Vehicle> getAllBuses() {
		try {
			TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v WHERE v.type = :busType GROUP BY v.id",
					Vehicle.class);
			query.setParameter("busType", Vehicle.Type.BUS);
			List<Vehicle> buses = query.getResultList();
			for (Vehicle bus : buses) {
				log.info(bus.toString());
			}
			return buses;
		} catch (Exception e) {
			log.error("Errore durante il recupero dei bus.", e);
			throw e;
		}
	}

	public List<Vehicle> getAllTrams() {
		try {
			TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v WHERE v.type = :tramType",
					Vehicle.class);
			query.setParameter("tramType", Vehicle.Type.TRAM);
			List<Vehicle> trams = query.getResultList();
			for (Vehicle tram : trams) {
				log.info(tram.toString());
			}
			return trams;
		} catch (Exception e) {
			log.error("Errore durante il recupero dei tram.", e);
			throw e;
		}
	}

	public void deleteVehicle(UUID id) {
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			Vehicle vehicle = em.find(Vehicle.class, id);
			if (vehicle != null) {
				em.remove(vehicle);
				transaction.commit();
				log.info("Veicolo eliminato correttamente: " + vehicle);
			} else {
				log.warn("Impossibile trovare il veicolo con ID: " + id);
			}
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error("Errore durante l'eliminazione del veicolo.", e);
			throw e;
		}
	}

	public void endService(UUID id) {
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();

			Vehicle vehicle = em.find(Vehicle.class, id);
			if (vehicle != null) {
				if (vehicle.getState() == State.IN_SERVICE) {
					vehicle.setState(State.UNDER_MAINTENANCE);
					vehicle.setMaintenanceStartDate(LocalDate.now());
					LocalDate endDate = LocalDate.now();
					Duration serviceDuration = Duration.between(vehicle.getServiceStartDate().atStartOfDay(),
							endDate.atStartOfDay());
					vehicle.setTotalServiceDuration(vehicle.getTotalServiceDuration().plus(serviceDuration));
					vehicle.setServiceCount(vehicle.getServiceCount() + 1);
				} else {
					log.info("Vehicle with ID " + id + " is not in service.");
				}
			} else {
				log.info("No vehicle found with ID " + id);
			}

			transaction.commit();
			log.info("Service ended successfully for vehicle with ID: " + id);
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error("Error occurred while ending service for vehicle with ID: " + id, e);
			throw e;
		}
	}

	public void endMaintenance(UUID id) {
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();

			Vehicle vehicle = em.find(Vehicle.class, id);
			if (vehicle != null) {
				if (vehicle.getState() == State.UNDER_MAINTENANCE) {
					vehicle.setState(State.IN_SERVICE);
					vehicle.setServiceStartDate(LocalDate.now());
					LocalDate endDate = LocalDate.now();
					Duration maintenanceDuration = Duration.between(vehicle.getMaintenanceStartDate().atStartOfDay(),
							endDate.atStartOfDay());
					vehicle.setTotalMaintenanceDuration(
							vehicle.getTotalMaintenanceDuration().plus(maintenanceDuration));
					vehicle.setMaintenanceCount(vehicle.getMaintenanceCount() + 1);
				} else {
					log.info("Vehicle with ID " + id + " is not under maintenance.");
				}
			} else {
				log.info("No vehicle found with ID " + id);
			}

			transaction.commit();
			log.info("Maintenance ended successfully for vehicle with ID: " + id);
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error("Error occurred while ending maintenance for vehicle with ID: " + id, e);
			throw e;
		}
	}

	public long docPerVehicleAndDate(Vehicle vehicle, LocalDate startDate, LocalDate endDate) {
		try {
			List<Ticket> tickets = getDocumentsByVehicle(vehicle);// fin qui tutto ok
			long count = tickets.stream()
					.filter(ticket -> isWithinDateRange(ticket.getDataVid(), startDate, endDate)).count();
			log.info("Numero di documenti vidimati per il mezzo selezionato nell'arco di tempo richiesto: " + count);
			return count;
		} catch (Exception e) {
			log.error("Si è verificato un errore durante il conteggio dei biglietti.", e);
			return 0;
		}
	}

	private List<Ticket> getDocumentsByVehicle(Vehicle vehicle) {
		try {
			List<Ticket> found = vehicle.getTicketsList();
			return found;
		} catch (Exception e) {
			log.error("Si è verificato un errore durante il conteggio dei documenti.", e);
			return null;
		}

	}

	private boolean isWithinDateRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
		return !date.isBefore(startDate) && !date.isAfter(endDate);
	}
	
	

}
