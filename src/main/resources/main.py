import settings
import discord
from discord.ext import commands
import os
import sys
from pathlib import Path

# Update the COGS_DIR path in settings or here
if getattr(sys, 'frozen', False):
    # If running as exe
    COGS_DIR = Path(sys._MEIPASS) / "cogs"
else:
    # If running in development
    COGS_DIR = Path(__file__).parent / "cogs"

logger = settings.logging.getLogger("bot")

TOKEN = os.getenv('DISCORD_TOKEN')
if not TOKEN:
    raise ValueError("No token provided. Please set the DISCORD_TOKEN environment variable.")
    
def run():
    intents = discord.Intents.all()
    # intents.guilds = True
    # intents.members = True
    # intents.message_content = True
    # intents.voice_states = True
    bot = commands.Bot(command_prefix="!", intents=intents)

    @bot.event
    async def on_ready():
        logger.info(f"User: {bot.user} (ID: {bot.user.id})")

        # Modified cogs loading to work with PyInstaller
        if getattr(sys, 'frozen', False):
            # If running as exe, manually specify cog files
            cog_files = [f for f in os.listdir(COGS_DIR) if f.endswith('.py') and f != "__init__.py"]
            for cog_file in cog_files:
                try:
                    await bot.load_extension(f"cogs.{cog_file[:-3]}")
                except Exception as e:
                    logger.error(f"Failed to load extension {cog_file}: {e}")
        else:
            # Development environment loading
            for cogs_file in COGS_DIR.glob("*.py"):
                if cogs_file.name != "__init__.py":
                    try:
                        await bot.load_extension(f"cogs.{cogs_file.name[:-3]}")
                    except Exception as e:
                        logger.error(f"Failed to load extension {cogs_file.name}: {e}")

        await bot.tree.sync()

    @bot.command()
    async def ping(ctx):
        await ctx.send("Pong!")

    # 봇 명령어 그룹 로드하는 코드
    @bot.command(hidden=True)
    async def load(ctx, cog: str):
        await bot.load_extension(f"cogs.{cog.lower()}")
        await ctx.send(f'Loaded {cog}')

    # 봇 명령어 그룹 언로드
    @bot.command(hidden=True)
    async def unload(ctx, cog: str):
        await bot.unload_extension(f"cogs.{cog.lower()}")
        await ctx.send(f'Unloaded {cog}')

    # 리로드
    @bot.command(hidden=True)
    async def reload(ctx, cog: str):
        await bot.reload_extension(f"cogs.{cog.lower()}")
        await ctx.send(f'Reloaded {cog}')

    bot.run(TOKEN, root_logger=True)


if __name__ == "__main__":
    run()
