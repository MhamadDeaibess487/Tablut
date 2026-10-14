# Audit du projet Tablut et plan de refactorisation

## Périmètre et méthode

Référence auditée : commit `5eeae0dcbe85086366c17fb8b6f1efc67b730cd0`, avant modifications. L'inventaire complet comprend **46 fichiers Java**, environ **7 474 lignes**, **82 images PNG**, **10 sauvegardes**, les fichiers de configuration/documentation, le script de tests et le JAR universitaire. Les deux rapports ont été lus : **Dossier IA.pdf, 6 pages**, et **Rapport_IHM.pdf, 2 pages**. Les couleurs du texte du rapport IHM ont également été examinées pour distinguer certaines propositions explicitement non implémentées.

Les sept programmes de test d'origine ont été compilés et exécutés avec succès avant refactorisation. Des essais ciblés supplémentaires ont reproduit une exception sur coordonnées invalides, un déplacement accepté depuis une case vide, un roi restant dans le plateau après capture et la perte du résultat dans le constructeur de copie. Un test passant ne suffisait donc pas à conclure à l'absence de défauts : `TestModel` contenait notamment l'expression tautologique `valide || !valide`.

Les 82 PNG ont été identifiés et vérifiés, les sections et enregistrements des dix sauvegardes ont été inspectés, et le manifeste/bytecode du JAR ont été examinés. Il est issu de Java 25 (version de classe 69), alors que les sources compilent avec Java 21. Les empreintes des rapports, images, sauvegardes et du JAR original ont été enregistrées avant les déplacements puis comparées après refactorisation.

## Fonctions présentes dans les sources

| Fonction | Preuve dans les classes | Portée réelle |
| --- | --- | --- |
| Plateau, déplacements orthogonaux, captures | `BaseJeu`, `Jeu` | Plateau 9 × 9, 16 noirs, huit blancs et un roi ; variante de capture spécifique |
| Victoire/fuite du roi et captures | `deplacer`, `CaptureBlanc`, `CaptureNoir`, `CaptureRoi` | Fuite sur tous les bords ; quatre voisins hostiles dans la zone centrale 3 × 3 ; sandwich ailleurs |
| Fin sans coup légal | `PlusDeCoup`, contrôleur et maintenant `Jeu` | Défaite du joueur bloqué, avec résultat explicite dans le modèle |
| Répétition | `jeuRedondant`, `draw`, historique `undo` | Nulle locale à trois occurrences du plateau ; le joueur actif n'entre pas dans la comparaison |
| Annulation/rétablissement | `Jeu.annuler`, `Jeu.refaire`, boutons Swing | Historique de plateaux ; pas un journal complet d'événements |
| Sauvegarde/chargement | Constructeur fichier, `sauvegardeFichier` | Plateau, joueur actif, pseudos et piles undo/redo ; ajout compatible d'un champ de résultat |
| Interface graphique | `MenuGraphique`, `FenetreJeu`, `GrilleGraphique` | Menu, plateau redimensionnable, sélection, destinations légales, image de fin de partie |
| Choix des joueurs/IA | `JoueurActifPanel`, `Controle`, `IA.creer` | IA activable indépendamment par camp, trois niveaux, humain/humain, humain/IA, IA/IA |
| Deux thèmes | `Config`, images des deux thèmes | Napoléon et Star Wars ; changement depuis le menu |
| Tutoriel | `TutorielPanel` | Onze pages, neuf illustrations dédiées et des images des thèmes |
| Multijoueur TCP | `ServerTablut`, `Client`, `Communication` | Deux clients par session, port 2121, coups, tours, annulation négociée, nulle négociée, abandon |

Aucun moteur d'apprentissage automatique, réseau neuronal, base de données, service web, authentification applicative, système de classement ou matchmaking n'est présent. Le serveur fonctionne pour une session de deux joueurs ; ce n'est pas une plateforme multijoueur multi-salles.

## Lecture critique des rapports

### Rapport IA

Le rapport décrit minimax/alpha-bêta, l'alternance MAX noirs / MIN blancs, le tri des coups, les évaluations positionnelles et une série de résultats historiques humains/IA. Ces éléments sont confrontés aux sources, sans reprendre les résultats historiques comme mesures actuelles.

| Sujet | Rapport | Code effectivement sélectionné |
| --- | --- | --- |
| Facile | Profondeur 1, sans élagage, matériel seulement | Alpha-bêta ; profondeur récursive 1 après le coup candidat ; routes du roi, protection, pression Manhattan ; pas de terme matériel |
| Intermédiaire | Profondeur 2, matériel et distance au bord | Profondeur récursive 1 après candidat ; matériel × 500, routes, protection, pression ; pas de terme explicite distance au bord |
| Difficile | Profondeur 3 et heuristique complète | Constante 3 après candidat : jusqu'à quatre demi-coups ; heuristique riche dans l'ancien `IAhardV2` |
| Danger des pièces | Poids 50 à un endroit, 35 dans le tableau | Poids 35 par menace dans le moteur difficile |
| Fork | Routes d'évasion assimilées à une forte menace | Le drapeau de route libre exclut les bloqueurs blancs mais peut accepter les noirs ; approximation stratégique conservée |
| Optimisations futures | Mémorisation, ouvertures | Aucune table de transposition ou bibliothèque d'ouvertures implémentée |
| Classement de force | Résultats de séries de dix parties | Pas de runner reproductible de ce benchmark dans le dépôt ; les tests actuels vérifient progression/correction |

Les trois niveaux sélectionnables sont `EasyAI`, `IntermediateAI`, `HardAI`. Les noms historiques correspondants étaient `IAeasy`, `IAinter`, `IAhardV2`. Les implémentations `IAhard`, `IAinterV5` et `IArandom` existaient mais n'étaient pas choisies par la factory : elles sont conservées comme `LegacyHardAI`, `LegacyIntermediateAI`, `RandomAI`.

Le matériel de l'heuristique compte la liste des pièces blanches, **roi inclus**. Le facile introduit un aléa dans la reconnaissance de la fuite du roi lors d'une évaluation ; les égalités de score sont départagées aléatoirement dans tous les niveaux. Les copies de recherche n'emportent pas les piles d'historique : la répétition locale n'est donc pas évaluée dans l'arbre. Aucun changement de profondeur, poids, choix aléatoire ou règle d'hostilité de l'heuristique n'a été introduit. Le cas du roi absent a été rendu sûr car le code facile pouvait déréférencer `null` après son test aléatoire.

### Rapport IHM

Le rapport fournit le contexte de conception, les questions de feedback/feedforward, le choix des thèmes et des retours de testeurs. Il indique que des réponses en bleu sont non implémentées. La surbrillance du dernier coup, la prévisualisation des captures et l'animation de réflexion sont effectivement décrites en bleu et absentes du code.

D'autres affirmations ne correspondent pas au dépôt : aucun mode daltonien ni son d'erreur n'a été retrouvé ; le slider utilise un style doré, sans interpolation vert/orange/rouge. Le README n'annonce donc pas ces fonctions. Les pseudos, indicateurs de joueur actif, surbrillances de destinations, thèmes, choix du niveau et images de victoire sont bien présents. Les deux rapports restent inchangés, y compris leurs formulations historiques.

## Architecture et responsabilités des 46 classes originales

Les packages actuels utilisent `tablut.*` en minuscules. Les noms métier français sont conservés ; seules les stratégies IA ont été renommées pour expliciter leur rôle.

| Classe originale → actuelle si renommée | Responsabilité et observation |
| --- | --- |
| `Tablut` | Point d'entrée ; création du modèle et du menu sur le thread Swing |
| `Config` | Taille du plateau, thèmes, chemins ; `load()` reste une méthode historique vide, pas un système de configuration persistante |
| `BaseJeu` | Règles, plateau, listes de pièces, résultats, captures, génération de coups et conversion bitboard ; trop de responsabilités mais aucun accès Swing/réseau |
| `Jeu` | Extension avec pseudos, sauvegardes, historique, alternance et répétition ; séparation de la persistance envisageable ensuite |
| `Point` | Coordonnées mutables ligne/colonne |
| `Pion` | Coordonnées et type d'une pièce capturable |
| `Coup` | Départ, destination et joueur d'un déplacement |
| `ListePoint` | Liste des positions par camp, déplacement/suppression et initialisation historique |
| `ListePion` | Collection temporaire de captures, méthodes historiques ; contient du code commenté ancien |
| `ListeCoup` | Collection de coups et compteur historique |
| `TableauBit` | Six longs : deux par catégorie noir/blanc/roi, représentant 81 cases ; comparaison lexicographique |
| `IA` | Factory des trois niveaux, choix d'un coup et application au modèle |
| `IAeasy` → `EasyAI` | Heuristique facile ; recherche partagée dans `AlphaBetaAI` |
| `IAinter` → `IntermediateAI` | Heuristique intermédiaire ; recherche partagée |
| `IAhardV2` → `HardAI` | Stratégie difficile réellement activée ; recherche partagée et évaluations supplémentaires |
| `IAhard` → `LegacyHardAI` | Ancienne stratégie, profondeur récursive locale 1 ; mobilité, routes, menaces, protection ; non exposée par la factory |
| `IAinterV5` → `LegacyIntermediateAI` | Expérience de profondeur 3 avec critères/commentaires exploratoires ; non exposée par la factory |
| `IArandom` → `RandomAI` | Baseline aléatoire, non exposée ; constructeur et boucle d'origine défectueux, remplacés par tirage dans les coups légaux |
| `ControleAbstract` | Contrat partagé d'interaction, accès au jeu, noms et options IA ; dépend d'une grille Swing |
| `Controle` | Sélection locale, mouvements, historique, fin de partie, sauvegarde, cadence et calcul IA |
| `ControleReseau` | Sélection côté client et demandes de coup/annulation/nulle/abandon |
| `ServerTablut` | État autoritaire d'une session, deux threads de lecture, validations, échanges d'approbation, fermeture des sockets |
| `Client` | Réception/envoi TCP et application des réponses ; reste couplé à Swing, les callbacks sont maintenant dispatchés sur l'EDT |
| `Communication` | Protocole numérique par lignes ; sérialisation de coups et bitboards, lecture/écriture de messages |
| `Request` | Type de requête et coup optionnel |
| `Reponse` | Type de réponse et plateau/charge utile optionnelle |
| `MenuGraphique` | Menu, options, chargement, choix du thème et accès Host/join |
| `FenetreJeu` | Fenêtre locale, pseudos, plateau, cartes de joueur, boutons et présentation de fin de partie |
| `FenetreJeuReseau` | Fenêtre réseau et lancement de la réception TCP |
| `GrilleGraphique` | Peinture du plateau, pièces, destinations et victoire ; conversion pixels/cases |
| `GrilleButtons` | Commandes locales et clics sur le plateau |
| `GrilleButtonsReseau` | Commandes multijoueur et clics sur le plateau |
| `JoueurActifPanel` | Cartes de joueur, portraits, noms, styles actifs et choix d'IA ; classe longue et susceptible de devenir plusieurs composants |
| `CustomSlider` | Rendu personnalisé du slider de difficulté |
| `RoundedButton` | Bouton à icônes avec variantes survol/appui et redimensionnement |
| `TutorielPanel` | Données des onze pages, navigation, texte et vues d'illustration |
| `ImageTutorielPanel` | Ancien panneau d'image de tutoriel ; conservé, mais non utilisé dans la navigation actuelle |
| `Chrono` | Décompte simple via `ActionListener` ; non utilisé, ne constitue pas une fonctionnalité de partie chronométrée |
| `TestModel` | Vérifications de base du modèle ; assertion tautologique remplacée par des attentes précises |
| `TestTableauBit` | Round-trip initial/après déplacement et recherche d'un coup d'ouverture |
| `TestCommunication` | Round-trip coup/requête/réponse |
| `TestReseauLocal` | Test TCP à deux sockets ; complété par requêtes invalides, négociation undo/nulle, déconnexion et arrêt |
| `TestAIEasy` | Premier coup et simulation IA/IA, limite de 500 coups |
| `TestAIInter` | Premier coup et simulation IA/IA, limite de 500 coups |
| `TestAIHard` | Premier coup et deux coups de simulation ; **pas une partie complète** |
| `TestAIUtils` | Helpers de simulation et suppression temporaire des traces |

Les nouvelles classes `AlphaBetaAI` et `ImageResources` partagent respectivement la recherche identique et le chargement fiable des images. Les nouveaux tests sont détaillés dans `VALIDATION.md`.

## Défauts constatés et corrections justifiées

| Défaut initial | Correction | Vérification |
| --- | --- | --- |
| Indexation du plateau avant validation des coordonnées | Validation bornes/null/source/destination avant lecture | `TestRules`, requête réseau avec destination 15 |
| Déplacements depuis une case vide ou d'un pion adverse | Refus dans le modèle, sans changement de plateau ni historique | `TestRules`, `TestReseauLocal` |
| Déplacements après fin de partie | Refus explicite | `TestRules`, `TestAIRegression` |
| Roi capturé conservé dans le plateau et la liste blanche | Suppression cohérente ; restauration du trône si nécessaire | Capture, serialization, undo/redo, IA et persistance |
| Résultat perdu lors des copies/redo/chargement | Copie de métadonnées, reconstruction terminale et champ `RESULTAT` compatible avec les anciens fichiers | `TestRules`, `TestPersistence` |
| Coup généré référençant les coordonnées mutables des pièces | Copie des coordonnées et joueur correspondant au camp demandé | Isolation de recherche et génération explorée par minimax |
| Lecture de sauvegarde sans fermeture du reader et erreurs silencieuses | Try-with-resources, validation du format/nom, remontée d'erreurs affichées par l'IHM | Fichiers invalides, absents, round-trip UTF-8 et dix sauvegardes anciennes |
| Duplication de deux fonctions de répétition | Comparaison commune via `jeuRedondant` ; une nulle ne remplace pas une victoire déjà obtenue | `TestRules` |
| Tutoriel dépendant des PNG sur disque | Ressources du classpath dans le JAR pour toutes les vues | `TestResources`, GUI depuis `/tmp` |
| Flux d'images laissés ouverts et décodage répété | Chargement commun avec fermeture, cache et erreur explicite si absente | 82 images du JAR ; les deux thèmes et pages de tutoriel |
| Résultat de redimensionnement d'icône ignoré | Utilisation effective de l'image redimensionnée | Vérification visuelle et test GUI |
| Calcul IA exécuté par un Timer sur l'EDT | `SwingWorker` sur copie, application sur EDT ; annulation lors des changements/fermetures | Test de réactivité et de fermeture pendant la recherche difficile |
| Timers persistant après `dispose()` | Arrêt explicite des timers et du travail en cours | Test GUI, terminaison normale du processus de test |
| Pseudos inversés à l'écran sans mise à jour du modèle | Affectation aléatoire des camps conservée et cohérente dans le modèle | Flow GUI et sauvegardes |
| Annulation du pseudo suivie d'une fenêtre vide rendue visible | Suppression d'un second `setVisible` redondant | Test GUI d'annulation |
| Perte du menu après échec d'ouverture réseau | Fermeture du menu après création réussie du client | Inspection du chemin d'erreur ; connexion graphique distante non testée |
| Serveur en boucle active, sockets non libérés après déconnexion, accès null pendant fermeture partielle | Attente des threads, interruption des négociations en cours et fermeture des sockets | Nulle négociée et déconnexion, threads terminés |
| Deux threads susceptibles d'entrelacer les lignes d'une réponse | Émission des messages complète sous un verrou de communication | Sérialisation et échanges TCP |
| Erreurs numériques réseau non traitées comme erreurs I/O | Conversion en `IOException` | Test de message numérique invalide |
| Réponses réseau appliquées depuis le thread de réception | Dispatch sur EDT | Compilation et inspection ; test GUI multijoueur avec deux fenêtres non réalisé |
| Script écrasant le JAR versionné et mélangeant sources/tests dans l'archive | Scripts dédiés, JAR dans `build/`, tests séparés | Build et test contre l'archive, absence des classes de tests dans celle-ci |
| `.gitignore` ne masquant pas `*.class` | Règles pour builds, IDE, temporaires et nouvelles sauvegardes | Vérification du statut Git |

## Plan appliqué et choix conservateurs

1. Créer `refactor/project-structure`, conserver la référence initiale et établir un résultat de tests avant modification.
2. Adopter `src/main/java`, `src/test/java`, `src/main/resources`, les packages minuscules `tablut.*`, `scripts/`, `docs/reports/` et `dist/legacy/`.
3. Séparer l'IA sans rendre publics les champs protégés du modèle : utilisation d'accesseurs et vues de collections. Partager le parcours alpha-bêta des trois moteurs actifs en conservant la différence de court-circuit terminal du moteur difficile.
4. Appliquer les corrections démontrées ci-dessus et ajouter des tests comportementaux. Conserver les heuristiques, les variantes de règles et les moteurs historiques.
5. Rédiger un README anglais, archiver les rapports sans modification, produire des captures du vrai programme et ajouter une recette CI.
6. Vérifier les chemins, compilation, tests, ressources embarquées, interface et intégrité des fichiers déplacés. Aucun push n'est effectué.

Les dix sauvegardes demeurent dans `res/saves` afin de rester accessibles au menu sans transformation de leurs noms ou contenus. Les nouvelles sauvegardes sont ignorées. Le placeholder historique dans `res/tests` et l'illustration supplémentaire `images_theme1` sont également conservés. Le script historique devient une redirection vers le nouveau runner.

Aucun outil de build externe n'était utilisé : les scripts JDK ont été rendus reproductibles plutôt que d'ajouter Maven ou Gradle. Le code n'a pas été traduit/reformaté en bloc, ni remplacé par un autre moteur de jeu. Aucun historique universitaire, rapport ou stratégie non sélectionnable n'a été supprimé.

## Limites restantes et suites possibles

- `BaseJeu`/`Jeu` restent volumineux ; la persistance et les résultats pourraient devenir des objets distincts dans une évolution séparée.
- Plateau, coordonnées et historiques restent mutables et accessibles ; les listes de pièces/coups proposées à l'IA sont non modifiables au niveau collection, mais les objets contenus ne sont pas immuables.
- Les collections métier maintiennent parfois un compteur en plus de leur taille. Les anciens noms/méthodes et certains commentaires exploratoires demeurent.
- Deux moteurs expérimentaux conservent leur duplication et ne sont pas exécutés par les tests des trois niveaux actifs. Ils ne sont pas annoncés comme des modes supplémentaires.
- La répétition compare uniquement le plateau ; les copies IA n'ont pas l'historique. Changer cela modifierait les règles/décisions et n'est pas inclus dans cette refactorisation.
- Les approximations de capture/hostilité du tri IA ne sont pas toutes identiques aux règles exactes du modèle. Le fork difficile peut surévaluer des routes bloquées par les noirs. Les weights et ces stratégies n'ont pas été recalibrés sans benchmark.
- Le niveau facile reste aléatoire ; les tests de partie complète ne sont pas une mesure statistique de force. La difficulté hard est testée sur progression limitée et positions tactiques, pas sur une partie complète.
- Le client réseau reste lié aux composants Swing ; pas de séparation complète transport/presentation. Connexion et dialogues sont synchrones ; le réseau ne fournit ni délai limite de négociation, ni salles, ni authentification, ni reconnexion.
- Le mode Host attend un second client. Fermer le menu avant les deux connexions peut laisser un serveur en attente ; une commande de cycle de vie de l'hôte est une évolution possible.
- Le chargement vérifie les records et le joueur actif, mais ne constitue pas un validateur exhaustif de toutes les positions artificiellement modifiées (chevauchements bitboard, nombre impossible de rois, etc.).
- Des sauvegardes historiques n'enregistrent pas explicitement le résultat. Il est reconstruit lorsque le plateau suffit ; un roi laissé sur le plateau par l'ancien bug peut rendre une ancienne capture terminale ambiguë.
- L'IHM reste partiellement bilingue et ses boutons/images n'ont pas fait l'objet d'un audit exhaustif d'accessibilité. Aucun mode daltonien ou indicateur visuel de calcul n'a été ajouté.
- Aucun fichier de licence n'était présent. Les droits des contributions collectives et des illustrations ne sont pas supposés ; aucune licence n'a été inventée.

Ces limites sont séparées des erreurs corrigées et des vérifications réussies. Elles n'empêchent pas la compilation et les workflows locaux vérifiés ; elles bornent ce qui peut être affirmé dans un portfolio.
