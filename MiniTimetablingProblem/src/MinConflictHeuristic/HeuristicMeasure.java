package MinConflictHeuristic;

public class HeuristicMeasure {

	Location location;
	public HeuristicMeasure(Location location) {
		this.location = location;
	}
	
	public int heuristicScore() {
		int score = 0;
		int rank4Count = 0;
		int rank3Count = 0;
		int rank2Count = 0;
		int boatDriverCount = 0;
		int crewmenCount = 0;
		int jetSkiUsersCount = 0;
		for (Employee[] ee : location.getTimetable()) {
			for (Employee e : ee) {
				int rank = e.getRank();
				if (rank == 4) rank4Count++;
				else if (rank == 3) rank3Count++;
				else if (rank == 2) rank2Count++;
				if (e.isBoatDriver()) boatDriverCount++;
				if (e.isBoatCrewman()) crewmenCount++;
				if (e.isjetSkiUser()) jetSkiUsersCount++;
			}
		}
		score += rank4Count < location.getrank4Req() ? location.getrank4Req() - rank4Count : 0;
		score += rank3Count < location.getrank3Req() ? location.getrank3Req() - rank3Count : 0;
		score += rank2Count < location.getrank2Req() ? location.getrank2Req() - rank2Count : 0;
		score += boatDriverCount < location.getBoatDriversReq() ? location.getBoatDriversReq() - boatDriverCount : 0;
		score += crewmenCount < location.getCrewmenReq() ? location.getCrewmenReq() - crewmenCount : 0;
		score += jetSkiUsersCount < location.getJetSkiUsersReq() ? location.getJetSkiUsersReq() - jetSkiUsersCount : 0;
		return score;
	}

}
