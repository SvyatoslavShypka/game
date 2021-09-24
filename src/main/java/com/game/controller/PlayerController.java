package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.impl.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/rest/players")
public class PlayerController {
    private final PlayerServiceImpl playerServiceImpl;

    @Autowired
    public PlayerController(PlayerServiceImpl playerServiceImpl) { this.playerServiceImpl = playerServiceImpl; }

    @GetMapping
    public List<Player> getSortAllPlayers(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) String title,
                                                @RequestParam(required = false) Race race,
                                                @RequestParam(required = false) Profession profession,
                                                @RequestParam(required = false) Long after,
                                                @RequestParam(required = false) Long before,
                                                @RequestParam(required = false) Boolean banned,
                                                @RequestParam(required = false) Integer minExperience,
                                                @RequestParam(required = false) Integer maxExperience,
                                                @RequestParam(required = false) Integer minLevel,
                                                @RequestParam(required = false) Integer maxLevel,
                                                @RequestParam(required = false) PlayerOrder order,
                                                @RequestParam(required = false) Integer pageNumber,
                                                @RequestParam(required = false) Integer pageSize) {

        List <Player> players = playerServiceImpl.getSortAllPlayers(name, title,race, profession,
                after, before, banned,
                minExperience, maxExperience, minLevel,
                maxLevel);

        return playerServiceImpl.getPlayersForPage(players, order, pageNumber, pageSize);
    }

    @GetMapping("/count")
    public Integer getCountSortAllPlayers(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String title,
                                        @RequestParam(required = false) Race race,
                                        @RequestParam(required = false) Profession profession,
                                        @RequestParam(required = false) Long after,
                                        @RequestParam(required = false) Long before,
                                        @RequestParam(required = false) Boolean banned,
                                        @RequestParam(required = false) Integer minExperience,
                                        @RequestParam(required = false) Integer maxExperience,
                                        @RequestParam(required = false) Integer minLevel,
                                        @RequestParam(required = false) Integer maxLevel) {

        return playerServiceImpl.getSortAllPlayers(name, title, race, profession,
                after, before, banned, minExperience, maxExperience,
                minLevel, maxLevel).size();
    }

    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable Long id) {
        return playerServiceImpl.getPlayerById(id);
    }

    @PostMapping
    @ResponseBody
    public Player createNewPlayer(@RequestBody Player newPlayer) {
        return playerServiceImpl.createNewPlayer(newPlayer);
    }

    @PostMapping("/{id}")
    @ResponseBody
    public Player editPlayer(@RequestBody Player newCharacteristics, @PathVariable Long id) {
        return playerServiceImpl.editPlayer(newCharacteristics, id);
    }

    @DeleteMapping("/{id}")
    public void deletePlayerById(@PathVariable Long id) {
            playerServiceImpl.deletePlayerById(id);
    }
}
