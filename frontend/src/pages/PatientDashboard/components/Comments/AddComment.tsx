import { Avatar, Button, Grid, IconButton, ListItem, TextField, useTheme } from "@mui/material"
import React, { useState } from "react"
import { useParams } from "react-router-dom"
import ClearIcon from "@mui/icons-material/Clear"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

const stringAvatar = (name: string) => {
  return `${name.split(" ")[0][0]}${name.split(" ")[1][0]}`
}

type Props = {
  author?: string
  onSubmit: () => void
}

export default function AddComment({ author, onSubmit }: Props) {
  const { CommentsApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const theme = useTheme()
  const { patientId } = useParams()
  const [focused, setFocused] = useState(false)
  const [comment, setComment] = useState("")

  const handleAdd = () => {
    CommentsApi.addComment(patientId!, {
      id: "", // FIXME should not be required on  "POST api/comments"
      patientId: patientId,
      comment: comment,
      highlighted: false,
    })
      .then(() => {
        showSuccessNotification("Kommentar hinzugefügt")
        setFocused(false)
        setComment("")
        onSubmit()
      })
      .catch(() => showErrorNotification("Fehler beim Hinzufügen des Kommentars"))
  }
  return (
    <ListItem>
      <Grid
        container
        direction={"row"}
        display={"flex"}
        width={"100%"}
        style={{ minHeight: "106px" }}
      >
        <Grid item margin={"10px"}>
          <Avatar sx={{ backgroundColor: theme.palette.primary.main }}>
            {author ? stringAvatar(author) : undefined}
          </Avatar>
        </Grid>
        <Grid item marginTop={"2px"} flexGrow={1}>
          <Grid direction={"column"}>
            <TextField
              focused={focused}
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              inputProps={{
                onFocus: () => setFocused(true),
                onBlur: (e) => {
                  if (e.target.value !== "") return
                  setFocused(false)
                },
              }}
              InputProps={{
                endAdornment: comment ? (
                  <IconButton
                    size="small"
                    onClick={() => {
                      setComment("")
                      setFocused(false)
                    }}
                  >
                    <ClearIcon />
                  </IconButton>
                ) : undefined,
                style: { minHeight: focused ? "43px" : undefined },
              }}
              multiline
              className={"NewCommentTextField"}
              label={"Kommentar Hinzufügen..."}
              variant={"standard"}
            />
            {focused && (
              <Grid container direction={"row"} justifyContent={"end"}>
                <Button
                  style={{ margin: "4px" }}
                  onClick={() => {
                    console.log("clicked")
                    handleAdd()
                  }}
                  variant={"contained"}
                >
                  Hinzufügen
                </Button>
              </Grid>
            )}
          </Grid>
        </Grid>
      </Grid>
    </ListItem>
  )
}
