package nx.multisystem.cmd;

import nx.multisystem.data.PlayerPropertyData;
import nx.multisystem.config.PropertySettings;
import nx.multisystem.gui.CoreGUI;
import nx.multisystem.language.MessageLanguage;
import nx.multisystem.util.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class PropertyPlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args){
		if(args.length == 0){
			if(!(sender instanceof Player)){
				sender.sendMessage(MessageLanguage.ONLY_PLAYER);
				return true;
			}
			openGUI((Player) sender);
			return true;
		}
		else if(args.length == 1 && args[0].equalsIgnoreCase("info")){
			if(!(sender instanceof Player)){
				sender.sendMessage(MessageLanguage.ONLY_PLAYER);
				return true;
			}
			sendPlayerInfo(sender, (Player) sender);
			return true;
		}
		else if(args.length == 2 && args[0].equalsIgnoreCase("info")){
			if(!sender.hasPermission("NcProperty.info.other")){
				sender.sendMessage(MessageLanguage.NOT_PERMISSION);
				return true;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			if(!target.isOnline()){
				sender.sendMessage(MessageLanguage.NOT_ONLINE);
				return true;
			}
			sendPlayerInfo(sender, (Player) target);
			return true;
		}
		else{
			sender.sendMessage(MessageLanguage.COMMAND_HELP_PLAYER);
			return true;
		}
	}

	private void openGUI(Player p){
		p.closeInventory();
		CoreGUI.openInterface(p);
	}

	private void sendPlayerInfo(CommandSender sender, Player target){
		PlayerPropertyData data = PlayerPropertyData.getPlayerData(target);
		int level = data.getLevel();
		int exp = data.getExp();
		int maxExp = PropertySettings.getMaxExp(level);

		NumberFormat probability = NumberFormat.getPercentInstance();
		probability.setMaximumFractionDigits(2);
		sender.sendMessage(
				new String[]{
						"§6[NcProperty]§f" + target.getDisplayName() + " §a的屬性資訊:",
						"§6等級(Level): " + level + "  §a經驗(EXP): " + percentFormat(exp, maxExp) + "  §e屬性點數(Point): " + data.getPoint(),
						"",
						"§c血量(HP): " + percentFormat(data.getHealth(), data.getMaxHealth()) + "  §e精力(MEN): " + percentFormat(data.getMentality(), data.getMaxMentality()) + "  §a耐力(VIT): " + percentFormat(data.getVitality(), data.getMaxVitality()),
						"§c血量回復: " + timeFormat(data.getRestoreHealth(), PropertySettings.getHealthRestoreTime()) +
								"§e  精力回復: " + timeFormat(data.getRestoreMentality(), PropertySettings.getMentalityRestoreTime()) +
								"§a  耐力回復: " + timeFormat(data.getRestoreVitality(), PropertySettings.getVitalityRestoreTime()),
						"",
						"§c物理攻擊(ATK): " + data.getAtk(),
						"§c物理防禦(DEF): " + data.getDef(),
						"§c物理閃避(AAR): " + probability.format(data.getAar()),
						"§c物理爆擊(AKB): " + probability.format(data.getAkb()),
						"§c物理命中(AHIT): " + probability.format(data.getAhit()),
						"",
						"§c✸力量(STR): " + data.getStr() + "  §b❋智力(INT): " + data.getInt() + "  §e☬敏捷(AGI): " + data.getAgi(),
						"§4☣幸運(LUK): " + data.getLuk() + "  §d✚體質(CON): " + data.getCon() + "  §2✤感知(WIS): " + data.getWis()
				}
		);
	}

	private String percentFormat(int a, int b){
		NumberFormat percent = StringFormat.percentFormat(1);
		return a + "/" + b + "(" + percent.format((double) a / b) + ")";
	}

	private String timeFormat(int value, long time){
		if(time >= 6000){
			time /= 1200;
			return value + "/" + time + "m";
		}
		else{
			time /= 20;
			return value + "/" + time + "s";
		}
	}
}

