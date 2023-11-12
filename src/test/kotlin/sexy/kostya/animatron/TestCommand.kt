package sexy.kostya.animatron

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.Suggestion
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.SetPassengersPacket
import net.minestom.server.resourcepack.ResourcePack
import net.minestom.server.timer.TaskSchedule
import sexy.kostya.animatron.integration.ModelledEntity
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

class TestCommand : Command("test") {

    init {
        val modelArg = ArgumentType.Word("model")
            .setSuggestionCallback { sender: CommandSender?, context: CommandContext?, suggestion: Suggestion ->
                val input = suggestion.input
                ModelEngine.registry().registeredModels.stream()
                    .filter(Predicate { name: String ->
                        name.lowercase().startsWith(input)
                    })
                    .forEach(Consumer { name: String? ->
                        suggestion.addEntry(
                            SuggestionEntry(name!!)
                        )
                    })
            }

        val viewArg = ArgumentType.Word("view")
            .setSuggestionCallback { sender: CommandSender?, context: CommandContext?, suggestion: Suggestion ->
                val input = suggestion.input
                ModelEngine.viewManager().registeredViews.stream()
                    .map { obj: Int -> obj.toString() }
                    .filter { id: String -> id.lowercase().startsWith(input) }
                    .forEach { id: String? ->
                        suggestion.addEntry(
                            SuggestionEntry(
                                id!!
                            )
                        )
                    }
            }

        val animationArg = ArgumentType.Word("animation")
            .setSuggestionCallback { sender: CommandSender?, context: CommandContext, suggestion: Suggestion ->
                val viewId = context.get(viewArg)
                val view = ModelEngine.viewManager().getView(viewId.toInt())
                if (view != null) {
                    val input = suggestion.input
                    view.model.animations.keys
                        .stream()
                        .filter { name: String ->
                            name.lowercase().startsWith(input)
                        }
                        .forEach { name: String? ->
                            suggestion.addEntry(
                                SuggestionEntry(name!!)
                            )
                        }
                }
            }

        addSubcommand(object : Command("summon") {
            init {
                addSyntax(playerExecutor { player: Player, context: CommandContext ->
                    val modelName = context.get(modelArg)
                    val model = ModelEngine.registry().getModel(modelName)
                    if (model == null) {
                        player.sendMessage(
                            Component.text(
                                "Unknown model: $modelName",
                                NamedTextColor.RED
                            )
                        )
                        return@playerExecutor
                    }
                    val entity = ModelledEntity(model)
                    entity.setInstance(
                        Objects.requireNonNull(player.instance, "player instance")!!,
                        player.position
                    )
                    val viewId = entity.entityId
                    player.sendPacket(SetPassengersPacket(entity.view.getBoneID("mount"), Collections.singletonList(player.entityId)))
                    var tick = 0
                    entity.scheduler().buildTask {
                        ++tick;
                        if (tick in 20..40) {
                            if (tick == 40) {
                                tick = 0;
                            }
                            return@buildTask
                        }
                        entity.refreshPosition(entity.position.withView(entity.position.yaw + 2F, entity.position.pitch))
                    }.repeat(TaskSchedule.tick(1)).schedule()
                    player.sendMessage(
                        Component.text()
                            .color(NamedTextColor.GREEN)
                            .content("Created model view with id: ")
                            .append(Component.text(viewId, NamedTextColor.DARK_GREEN))
                    )
                }, modelArg)
            }
        })

        addSubcommand(object : Command("play") {
            init {
                addSyntax(playerExecutor { player: Player, context: CommandContext ->
                    val viewId = context.get(viewArg)
                    val animationName = context.get(animationArg)
                    val view = ModelEngine.viewManager().getView(viewId.toInt())
                    if (view == null) {
                        player.sendMessage("Unknown view: $viewId")
                        return@playerExecutor
                    }
                    view.playAnimation(animationName)
                }, viewArg, animationArg)
            }
        })

        addSubcommand(object : Command("stop") {
            init {
                addSyntax(playerExecutor { player: Player, context: CommandContext ->
                    val viewId = context.get(viewArg)
                    val animationName = context.get(animationArg)
                    val view = ModelEngine.viewManager().getView(viewId.toInt())
                    if (view == null) {
                        player.sendMessage("Unknown view: $viewId")
                        return@playerExecutor
                    }
                    view.stopAnimation(animationName)
                }, viewArg, animationArg)
            }
        })

        addSubcommand(object : Command("stop_all") {
            init {
                addSyntax(playerExecutor { player: Player, context: CommandContext ->
                    val viewId = context.get(viewArg)
                    val view = ModelEngine.viewManager().getView(viewId.toInt())
                    if (view == null) {
                        player.sendMessage("Unknown view: $viewId")
                        return@playerExecutor
                    }
                    view.stopAllAnimations()
                }, viewArg)
            }
        })

        addSubcommand(object : Command("tphere") {
            init {
                addSyntax(playerExecutor { player: Player, context: CommandContext ->
                    val viewId = context.get(viewArg)
                    val view = ModelEngine.viewManager().getView(viewId.toInt())
                    if (view == null) {
                        player.sendMessage("Unknown view: $viewId")
                        return@playerExecutor
                    }
                    view.entity.teleport(player.position)
                }, viewArg)
            }
        })

        addSubcommand(object : Command("pack") {
            init {
                addSubcommand(
                    playerCommand("apply"
                    ) { player: Player, _: CommandContext? ->
                        player.setResourcePack(
                            ResourcePack.forced(
                                "http://localhost:7272/?v=" + kotlin.random.Random.nextInt(),
                                null,
                                Component.text("Hello! Please accept the resource-pack")
                            )
                        )
                    }
                )
            }
        })
    }

    companion object {
        private fun playerExecutor(executor: (Player, CommandContext) -> Unit): CommandExecutor {
            return CommandExecutor { sender, ctx ->
                if (sender !is Player) {
                    sender.sendMessage("Only players can execute this command")
                    return@CommandExecutor
                }
                executor(sender, ctx)
            }
        }

        private fun playerCommand(name: String, executor: (Player, CommandContext) -> Unit): Command {
            return object : Command(name) {

                init {
                    defaultExecutor = playerExecutor(executor)
                }

            }
        }
    }

}