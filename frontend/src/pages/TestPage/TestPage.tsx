import { FormProvider, useForm } from "react-hook-form"
import { TextField } from "./Components/TextField"
import { Button, Divider } from "@mui/material"
import Select from "./Components/Select"

export default function TestPage() {
  const form = useForm<{ textField1: ""; textField2: "" }>()
  const methods = form

  return (
    <>
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit((data) => console.log(data))}>
          <TextField
            name={"textField1"}
            label={"TextField1"}
            validation={{ required: "Erforderlich" }}
          />
          <TextField
            name={"textField2"}
            label={"TextField2"}
            validation={{ required: { value: true, message: "Erforderlich" } }}
          />
          <Divider />
          <Select
            name={"select1"}
            label={"Select1"}
            validation={{ required: "Erforderlich" }}
            options={[
              { label: "Option 1", value: "1" },
              { label: "Option 2", value: "2" },
            ]}
          />
          <Select
            name={"select2"}
            label={"Select2"}
            validation={{ required: { value: true, message: "Erforderlich" } }}
            options={[
              { label: "Option 1", value: "1" },
              { label: "Option 2", value: "2" },
            ]}
          />
          <Button type="submit">Submit</Button>
        </form>
      </FormProvider>
    </>
  )
}
