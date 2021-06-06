package net.volcano.jdacommands.client;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.volcano.jdacommands.constants.Reactions;
import net.volcano.jdacommands.model.menu.Confirmation;
import net.volcano.jdacommands.model.menu.pagers.Menu;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ReactionMenuClient extends ListenerAdapter {
	
	private final ConcurrentHashMap<String, Menu> menus = new ConcurrentHashMap<>();
	
	public ReactionMenuClient() {
		
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(this::removeOld, 10, 10, TimeUnit.MINUTES);
		
		log.info("Initialized reaction menu client.");
		
	}
	
	public void register(Menu menu) {
		if (menu.getExpiration() <= 0) {
			throw new IllegalArgumentException("Expiration cannot be equal to, or lower than, zero.");
		}
		if (menu.getMessageId() == null) {
			throw new IllegalArgumentException("Message id cannot be null.");
		}
		
		menus.put(menu.getMessageId(), menu);
		menu.registered(this);
	}
	
	public void unregister(Menu menu) {
		menus.remove(menu.getMessageId());
	}
	
	/**
	 * Remove all expired menus
	 */
	public void removeOld() {
		long now = Instant.now().toEpochMilli();
		menus.forEach((id, menu) -> {
			if (menu.getCreationTime() + menu.getExpiration() < now) {
				menus.remove(id);
			}
		});
	}
	
	@Override
	public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {
		if (menus.containsKey(event.getMessageId())) {
			
			menus.get(event.getMessageId())
					.onReactionEvent(event);
		}
	}
	
	public CompletableFuture<Boolean> askConfirmation(String content, MessageChannel channel, String userId) {
		
		Confirmation confirmation = new Confirmation(userId, null);
		
		channel.sendMessage(Confirmation.getEmbed(content).build())
				.queue(message -> {
					message.addReaction(Reactions.YES).queue();
					message.addReaction(Reactions.NO).queue();
					confirmation.setMessageId(message.getId());
					register(confirmation);
				});
		
		return confirmation.getFuture();
	}
	
	public CompletableFuture<Boolean> askConfirmation(String content, MessageChannel channel, User user) {
		return askConfirmation(content, channel, user.getId());
	}
	
}


