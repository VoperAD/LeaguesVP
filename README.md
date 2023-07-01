<h1 align="center">Leagues VP</h1>

![Leagues VP Logo - Imgur](https://github.com/VoperAD/LeaguesVP/assets/92862848/2d47918d-3fa1-4088-b198-42e3d3b79c46)

#### Download Link
- <a href="https://www.spigotmc.org/resources/leaguesvp.110922/">Download from Spigot</a>

LeaguesVP is a simple addon for the plugin <a href="https://github.com/RoinujNosde/SimpleClans">Simple Clans</a> that implements
a league system that can be used at the discretion of server owners. The main objective of this addon is to create a more competitive 
and at the same time versatile atmosphere, as the owners will decide when and how the scores will be assigned to each clan.

# Commands

It is possible to change the aliases of the commands in the `config.yml` file.

### Admin Permission:
- `/leaguesvp givepoints <player> <amount>` - Give points to a player's clan
- `/leaguesvp giveall <amount>` - Give points to all the clans
- `/leaguesvp removepoints <player> <amount>` - Remove points from a player's clan
- `/leaguesvp removeall <amount>` - Remove points from all the clans
- `/leaguesvp resetpoints <player>` - Reset the points of a player's clan
- `/leaguesvp resetall` - Reset the points of all the clans
- `/leaguesvp register <clan>` - Manually registers a clan 
- `/leaguesvp unregister <clan>` - Manually unregisters a clan

### Anyone Permission:
- `/leaguesvp info <clan>` - See the amount of points a clan has
- `/leaguesvp top <page>` - View the clan rank. The parameter <page> is optional.

# Placeholders

Currently, there are just two placeholders that can be used by server owners.
Feel free to give us any suggestion.

- `%leaguesvp_cpoints%` - Show the amount of points of a player's clan
- `%leaguesvp_top_position%` - Show the clan position in the rank

# :framed_picture: Screenshots 

![Captura de Tela 2023-07-01 às 16 07 38](https://github.com/VoperAD/LeaguesVP/assets/92862848/15124866-95f0-42ae-95d4-ec4901045131)
![Captura de Tela 2023-07-01 às 20 19 24](https://github.com/VoperAD/LeaguesVP/assets/92862848/7b2843d6-9def-4868-9bb8-95e77a5b3499)

# Credits

First of all, I would like to thank RoinujNosde, who solved many of my doubts when I
was developing this plugin. Also, thanks to the AbsolutGG server, where I was able to 
perform the necessary tests for the plugin.

I really hope everyone enjoys the addon and if you have any doubts or for some reason want
to get in touch with me, feel free to dm me in discord `_voper`. For anyone who may wonder, this was
the first plugin I developed. I made it in 2022 and decided now to make it public :).
