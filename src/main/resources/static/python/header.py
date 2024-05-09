import settings

#[D] = Discord
import discord
from discord.ext import commands

#[L] = Logger
logger = settings.logging.getLogger("bot")

#[F] = Feature
import random                                   # For the command "!choices".


def run():
    intents = discord.Intents.all()                     # Enable all type of intents. Only for developing.

    # Choose intent types to enable
    # intents = discord.Intents.default()
    # intents.message_content = True # Enable message intents

    bot = commands.Bot(command_prefix="!", intents=intents)