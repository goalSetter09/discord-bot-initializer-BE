    @bot.command()
    async def say(ctx, what = "WHAT?"):                     # Only take single word. Default "WHAT?."
        await ctx.send(what)                                # Reply with it.