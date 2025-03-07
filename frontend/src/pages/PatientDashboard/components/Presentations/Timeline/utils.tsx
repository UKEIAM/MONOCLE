export const reverse = <T,>(array: T[]): T[] => {
  const clone = [...array]
  return clone.reverse()
}
