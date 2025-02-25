import React from "react"

export function useSafeContext<TContext>(
  context: React.Context<TContext | undefined>,
  valueName: string,
): TContext {
  const contextValue = React.useContext(context)

  if (!contextValue) {
    throw new Error(`No ${valueName} context value was provided.`)
  }

  return contextValue
}
