package com.game.service.impl;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.NotFoundException;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//Main business logic.
//We use Repository, wrappers methods of JpaRepository's ones;
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    //  Get whole sorted list of Players according to received filters
    @Override
    public List<Player> getSortAllPlayers(String name, String title, Race race, Profession profession, Long after,
                                                Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                                Integer minLevel, Integer maxLevel) {

        List<Player> sortAllPlayers = playerRepository.findAll();

        if (name != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getName().contains(name))
                    .collect(Collectors.toList());
        }

        if (title != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getTitle().contains(title))
                    .collect(Collectors.toList());
        }

        if (race != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getRace().equals(race))
                    .collect(Collectors.toList());
        }

        if (profession != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getProfession().equals(profession))
                    .collect(Collectors.toList());
        }

        if (after != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getBirthday().getTime() >= after)
                    .collect(Collectors.toList());
        }

        if (before != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getBirthday().getTime() <= before)
                    .collect(Collectors.toList());
        }

        if (banned != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.isBanned().equals(banned))
                    .collect(Collectors.toList());
        }

        if (minExperience != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getExperience() >= minExperience)
                    .collect(Collectors.toList());
        }

        if (maxExperience != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getExperience() <= maxExperience)
                    .collect(Collectors.toList());
        }

        if (minLevel != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getLevel() >= minLevel)
                    .collect(Collectors.toList());
        }

        if (maxLevel != null) {
            sortAllPlayers = sortAllPlayers.stream()
                    .filter(s -> s.getLevel() <= maxLevel)
                    .collect(Collectors.toList());
        }

        return sortAllPlayers;
    }

    // Get list with Pagination.
    // Use Stream: skip /other pages/ and limit records on the page. All sorted
    @Override
    public List<Player> getPlayersForPage(List<Player> sortAllPlayers, PlayerOrder order,
                                      Integer pageNumber, Integer pageSize) {

        if (order == null) order = PlayerOrder.ID;
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;
//was added recommended cast to long
        return sortAllPlayers.stream()
                .sorted(getComparator(order))
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    //  Create new Player
    @Override
    public Player createNewPlayer(Player newPlayer) {

    //Ignore ID, Level and UntilNextLevel for update and create
        if (newPlayer.getName() == null
                || newPlayer.getTitle() == null
                || newPlayer.getRace() == null
                || newPlayer.getProfession() == null
                || newPlayer.getBirthday() == null
                || newPlayer.getExperience() == null) {

            throw new BadRequestException();
        }

        if (newPlayer.getName().length() > 12 || newPlayer.getTitle().length() > 30
                || newPlayer.getName().isEmpty() || newPlayer.getTitle().isEmpty()
                || newPlayer.getExperience() < 0 || newPlayer.getExperience() > 10000000
                || !isBirthdayCorrect(newPlayer)) {

            throw new BadRequestException();
        }

        if (newPlayer.isBanned() == null) {
            newPlayer.setBanned(false);
        }

        newPlayer.setLevel(playerLevelCalculation(newPlayer));

        newPlayer.setUntilNextLevel(playerUntilNextLevelCalculation(newPlayer));
        return playerRepository.save(newPlayer);
    }

    //  Edit Player's fields
    @Override
    public Player editPlayer(Player newCharacteristics, Long id) {

        //Ignore ID, Level and UntilNextLevel for update and create
        // Error 400 incorrect input
        if (!isThisTheCorrectId(id)) throw new BadRequestException();

        // Error 404 such Player no found
        if (!playerRepository.existsById(id)) throw new NotFoundException();

        // Get Player id and make edit according to newCharacteristics
        Player editablePlayer = getPlayerById(id);

        // Name
        String name = newCharacteristics.getName();
        if (name != null) {
            if (name.length() > 12 || name.isEmpty()) throw new BadRequestException();

            editablePlayer.setName(name);
        }

        // Title
        String title = newCharacteristics.getTitle();

        if (title != null) {
            if (title.length() > 30 || title.isEmpty()) throw new BadRequestException();

            editablePlayer.setTitle(title);
        }

        // Race
        if (newCharacteristics.getRace() != null) {
        String race = newCharacteristics.getRace().toString();
            if (race.isEmpty()) throw new BadRequestException();
            editablePlayer.setRace(newCharacteristics.getRace());
        }

        // Profession
        if (newCharacteristics.getProfession() != null) {
        String profession = newCharacteristics.getRace().toString();
            if (profession.isEmpty()) throw new BadRequestException();
            editablePlayer.setProfession(newCharacteristics.getProfession());
        }

        // Birthday
        if (newCharacteristics.getBirthday() != null) {
        String birthday = newCharacteristics.getBirthday().toString();
            if (birthday.isEmpty() || !isBirthdayCorrect(newCharacteristics)) throw new BadRequestException();
            editablePlayer.setBirthday(newCharacteristics.getBirthday());
        }

        // isBanned
//TODO Check if this field is correct (0/1, true/false)
        if (newCharacteristics.isBanned() != null) {
        String banned = newCharacteristics.isBanned().toString();
            if (banned.isEmpty()) newCharacteristics.setBanned(false);
            editablePlayer.setBanned(newCharacteristics.isBanned());
        }

        // Experience
        if (newCharacteristics.getExperience() != null) {
        String experience = newCharacteristics.getExperience().toString();
            if (experience.isEmpty()) throw new BadRequestException();
            if (newCharacteristics.getExperience() < 0 || newCharacteristics.getExperience() > 10000000) throw new BadRequestException();

            editablePlayer.setExperience(newCharacteristics.getExperience());
        }
// Ignore null positions for update

        editablePlayer.setLevel(playerLevelCalculation(editablePlayer));

        editablePlayer.setUntilNextLevel(playerUntilNextLevelCalculation(editablePlayer));

        return editablePlayer;
    }

    //  Edit Player by ID;
    @Override
    public void deletePlayerById(Long id) {
        if (!isThisTheCorrectId(id)) throw new BadRequestException();

        if (!playerRepository.existsById(id)) throw new NotFoundException();

        playerRepository.deleteById(id);
    }

    //  Get Player by ID;
    @Override
    public Player getPlayerById(Long id) {
        if (!isThisTheCorrectId(id)) throw new BadRequestException();

        if (!playerRepository.existsById(id)) throw new NotFoundException();

        return playerRepository.findById(id).orElse(null);
    }

    // Additional methods
    // 1. Order process
    private Comparator<Player> getComparator(PlayerOrder order) {
        if (order == null) {
        // If order is NULL - we use PlayerOrder.ID
            return Comparator.comparing(Player::getId);
        }

        Comparator<Player> comparator = null;
        //TODO check if there are all switch fields to sort
        switch (order.getFieldName()) {
            case "id":
                comparator = Comparator.comparing(Player::getId);
                break;
            case "name":
                comparator = Comparator.comparing(Player::getName);
                break;
            case "experience":
                comparator = Comparator.comparing(Player::getExperience);
                break;
            case "birthday":
                comparator = Comparator.comparing(Player::getBirthday);
                break;
            case "level":
                comparator = Comparator.comparing(Player::getLevel);
        }

        return comparator;
    }

    // 2. Level Calculation
    private Integer playerLevelCalculation(Player player) {
        double levelCalculation = (Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
        return (int) levelCalculation;
    }

    // 3. UntilNextLevel Calculation
    private Integer playerUntilNextLevelCalculation(Player player) {
        double untilNextLevelCalculation = 50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience();
        return (int) untilNextLevelCalculation;
    }

    // 4. Is ID correct?
    private Boolean isThisTheCorrectId(Long id) {
        return id != null &&
                id > 0 &&
                id == Math.floor(id);
    }

    // 5. Is date correct?
    private Boolean isBirthdayCorrect(Player player) {
//TODO check on mistake to precise Date min in milliseconds 2000 and 3000 years. Use Date()
    // If date < 1970 year - throw exception
        if (player.getBirthday().getTime() < 0) throw new BadRequestException();
        Calendar cal = Calendar.getInstance();
        cal.setTime(player.getBirthday());
        int year = cal.get(Calendar.YEAR);

    // If date out of 2000-3000 years - throw exception
        if (year < 2000 || year > 3000) throw new BadRequestException();

        return true;
    }
}
