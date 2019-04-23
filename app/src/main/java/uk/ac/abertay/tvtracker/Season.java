package uk.ac.abertay.tvtracker;

import java.util.ArrayList;

public class Season implements Comparable<Season> {
    private final int season_numner;
    private final ArrayList<Episode> episodes;

    Season(int season_numner) {
        episodes = new ArrayList<>();
        this.season_numner = season_numner;
    }

    Season(ArrayList<Episode> episodes, int season_numner) {
        this.episodes = episodes;
        this.season_numner = season_numner;
    }

    public void add_episode(Episode ep) {
        episodes.add(ep);
    }

    public int get_number_of_episodes() {
        return episodes.size();
    }

    public int get_number_of_watched_episodes() {
        int count = 0;
        for(int i = 0; i < episodes.size(); i++) {
            if(episodes.get(i).is_watched()) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<String> get_episode_names() {
        ArrayList<String> names = new ArrayList<>();
        for(int i = 0; i < episodes.size(); i++) {
            names.add(episodes.get(i).get_name());
        }
        return names;
    }

    public int get_season_number() {
        return this.season_numner;
    }

    @Override
    public int compareTo(Season s) {
        return (this.get_season_number() < s.get_season_number() ? -1 : 1);
    }
}
