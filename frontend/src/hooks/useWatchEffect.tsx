import { Control, useWatch } from "react-hook-form"
import useDeepCompareEffect from "use-deep-compare-effect"

const useWatchEffect = <T,>(
  watchProps: {
    name: string
    defaultValue?: any
    control?: Control | undefined
    disabled?: boolean | undefined
    exact?: boolean | undefined
  },
  callback: (subscription: T) => void | (() => void),
) => {
  const subscription = useWatch(watchProps)
  // recalculate rows every time/only when molecularTherapies change
  useDeepCompareEffect(() => {
    callback(subscription)
  }, [subscription])
}

export default useWatchEffect
