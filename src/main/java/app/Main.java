package app;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import dao.CardDAO;
import dao.DealersDAO;
import dao.RouteDAO;
import dao.Travel_DocumentDAO;
import dao.UserDAO;
import dao.VehicleDAO;
import entities.Ticket;
import entities.User;
import entities.Vehicle;
import lombok.extern.slf4j.Slf4j;
import util.JpaUtil;

@Slf4j
public class Main {
	public static void main(String[] args) {
		EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		UserDAO userDAO = new UserDAO(em);
		CardDAO cardDAO = new CardDAO(em);
		Travel_DocumentDAO travelDAO = new Travel_DocumentDAO(em);
		DealersDAO dealersDAO = new DealersDAO(em);
		VehicleDAO vDAO = new VehicleDAO(em);
		RouteDAO rDAO = new RouteDAO(em);
		
		//date pronte per controlli metodi
		LocalDate data1 = LocalDate.now().minusDays(2);
		LocalDate data2 = LocalDate.now();
		
		
		//MANDO IN MANTENIMENTO QUALCHE MEZZO
//		vDAO.endService(UUID.fromString("1d61eddf-6cbf-4d4f-bd81-79842f7cff11"));
//		vDAO.endService(UUID.fromString("614b32a1-1a15-45b1-9d1a-94d844c3bf19"));
//		vDAO.endService(UUID.fromString("95c1f118-f72e-4afa-b7bb-781ae8b031d2"));
//		vDAO.endService(UUID.fromString("fc8e86d0-8b46-4757-a045-6d1c02fd738b"));
		// RIMANDO IN SERVIZIO I MEZZI
//		vDAO.endMaintenance(UUID.fromString("614b32a1-1a15-45b1-9d1a-94d844c3bf19"));
//		vDAO.endMaintenance(UUID.fromString("95c1f118-f72e-4afa-b7bb-781ae8b031d2"));
		
		// SCANNER PER INTERAZIONE CON UTENTE
		Scanner scanner = new Scanner(System.in);

		int input = -1;

		while (input != 0) {
			System.out.println("Premi 1 per visualizzare tutti gli utenti");
			System.out.println("Premi 2 per visualizzare tutte le tessere");
			System.out.println("Premi 3 per visualizzare tutti i titoli di viaggio");
			System.out.println("Premi 4 per visualizzare tutti i mezzi in servizio");
			System.out.println("Premi 5 per visualizzare tutti i mezzi in manutenzione");
			System.out.println("Premi 6 per visualizzare tutti i rivenditori");
			System.out.println("Premi 7 per visualizzare tutte le tratte");
			System.out.println("Premi 8 per visualizzare altre opzioni");
			System.out.println("Premi 9 per una sorpresa!");
			System.out.println("Premi 0 per chiudere!");

			input = scanner.nextInt();
			switch (input) {
			case 1:
				userDAO.getAllUser();
				break;

			case 2:
				cardDAO.getAllCard();
				break;

			case 3:
				travelDAO.getAllTd();
				break;

			case 4:
				 vDAO.getAllVehiclesInService();
				break;

			case 5:
				vDAO.getAllVehiclesUnderMaintenance();
				break;

			case 6:
				dealersDAO.getAllAuthorizedDealers();
				break;

			case 7:
				rDAO.getAllRoutes();
				break;

			case 8:
			    int subInput = -1;
			    while (subInput != 0) {
			        System.out.println("Premi 1 per vedere biglietti e abbonamenti emessi in un dato periodo di tempo in totale e per punto di emissione");
			        System.out.println("Premi 2 per check validità abbonamento in base a numero di tessera utente");
			        System.out.println("Premi 3 per check numero biglietti vidimati su un mezzo per un dato periodo di tempo");
			        System.out.println("Premi 4 per associare in automatico il veicolo più veloce ad ogni tratta");
			        System.out.println("Premi 5 per vedere quale veicolo ha timbrato più biglietti in assoluto");
			        System.out.println("Premi 0 per tornare al menu principale");

			        subInput = scanner.nextInt();
			        switch (subInput) {
			            case 1:
			                travelDAO.docPerDealersAndDate(dealersDAO.getAuthorizedDealerById("92c0c3d3-94f7-45a5-991b-b3e8f17f2e0f"), data1, data2);
			                break;

			            case 2:
			                travelDAO.checkValidity(travelDAO.findByUUID("550ea942-3534-43a1-8436-c5f09482701a").getId().toString(), cardDAO.findById("07bead1e-4e41-48b6-a57b-b204588e13d1").getId().toString());
			                break;

			            case 3:
			                vDAO.docPerVehicleAndDate(vDAO.getVehicleById(UUID.fromString("307d98a3-59db-4910-8eee-0d8aebcb31b0")), data1, data2.plusDays(2));
			                break;

			            case 4:
			                rDAO.findVehicleIdsWithShortestTravelTime();
			                break;

			            case 5:
			                vDAO.findVehicleWithHighestTicketsValidated();
			                break;

			            case 0:
			                break;

			            default:
			                System.out.println("Opzione non valida. Riprova.");
			                break;
			        }
			    }
			    break;

			case 9:
				
				//creazione utente
				System.out.println("Crea un nuovo utente, inserisci i dati come richiesto: ");
				User user = new User();

				System.out.println("Inserisci il nome dell'utente: ");
				String firstName = scanner.next();
				user.setName(firstName);

				System.out.println("Inserisci il cognome dell'utente: ");
				String lastName = scanner.next();
				user.setLastName(lastName);

				userDAO.saveUser(user);
				
				// Acquisto del biglietto
				System.out.println("Acquista un biglietto inserendo la data: ");
				System.out.println("Inserisci la data del biglietto (AAAA-MM-GG): ");
				String ticketDateStr = scanner.next();
				LocalDate ticketDate = LocalDate.parse(ticketDateStr);
				Ticket ticket = new Ticket(ticketDate, dealersDAO.getAuthorizedDealerById("92c0c3d3-94f7-45a5-991b-b3e8f17f2e0f"), false, user);
				travelDAO.save(ticket);
				
				// Scelta dell'autobus
				List<Vehicle> buses = vDAO.getAllVehiclesInService();
				System.out.println("Seleziona il mezzo disponibile:");

				int busIndex = 1;
				for (Vehicle bus : buses) {
				    System.out.println(busIndex + ". " + bus.toString());
				    busIndex++;
				}
				
				System.out.print("Inserisci il numero corrispondente all'autobus scelto: ");
				int selectedBusIndex = scanner.nextInt();
				scanner.nextLine();
				
				Vehicle selectedBus = buses.get(selectedBusIndex - 1);
				System.out.println("Sei salito sul " + selectedBus.getType() + ", quando il controllore ti chiede di mostrare il tuo titolo di viaggio...");
				
				if(user.getName().equals("Ajeje") && user.getLastName().equals("Brazorf")) {
					
					ticket.setEndorsed(true);//scherzetto...
				
					vDAO.validateTicket(ticket.getId().toString(), selectedBus.getId().toString());
				}else {
					vDAO.validateTicket(ticket.getId().toString(), selectedBus.getId().toString());
				}
				
				
				break;

			case 0:
				System.out.println("Grazie per averci scelto!");
				break;

			default:
				System.out.println("Devi inserire un numero da 0 a 9.");
			}
			;
		}

		// CHIUSURA SCANNER ED ENTITY MANAGER
		scanner.close();
		em.close();
		emf.close();
	}

}
